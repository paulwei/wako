## docker login --username=大林小莉2021 registry.cn-hangzhou.aliyuncs.com  Paul省份
## docker pull registry.cn-hangzhou.aliyuncs.com/google_containers/busybox
## docker pull redis:5.0.13
## Kubeadm 在2018年12月3日发布的Kubernetes 1.13 版本中已经宣布GA，可以支持生产。
#!/bin/bash
# 关闭防火墙
systemctl stop firewalld
systemctl disable firewalld
# 关闭selinux
# 永久关闭
sed -i 's/enforcing/disabled/' /etc/selinux/config
# 临时关闭
setenforce 0
# 关闭swap
# 临时
swapoff -a
# 永久关闭
sed -ri 's/.*swap.*/#&/' /etc/fstab
# 根据规划设置主机名【master节点上操作】
hostnamectl set-hostname k8s_master
#根据规划设置主机名【node1节点操作】
#hostnamectl set-hostname k8s_node1
# 在master添加hosts
cat >>/etc/hosts <<EOF
192.168.1.10 k8s_master
192.168.1.11 k8s_node1
EOF
# 将桥接的IPv4流量传递到iptables的链
cat >/etc/sysctl.d/k8s.conf <<EOF
#开启网桥模式【重要】
net.bridge.bridge-nf-call-iptables=1
#开启网桥模式【重要】
net.bridge.bridge-nf-call-ip6tables=1
net.ipv4.ip_forward=1
net.ipv4.tcp_tw_recycle=0
# 禁止使用 swap 空间，只有当系统 OOM 时才允许使用它
vm.swappiness=0
# 不检查物理内存是否够用
vm.overcommit_memory=1
#关闭ipv6【重要】
net.ipv6.conf.all.disable_ipv6=1
net.netfilter.nf_conntrack_max=2310720
EOF
# 生效
sysctl --system
# 时间同步
yum install ntpdate -y
ntpdate time.windows.com

##群内节点SSH登录免密
##登录k8s-master
ssh-keygen -t rsa
ssh-copy-id -i ~/.ssh/id_rsa.pub root@192.168.1.11
ssh-copy-id -i ~/.ssh/id_rsa.pub root@192.168.1.12
##登录k8s-node1
ssh-keygen -t rsa
ssh-copy-id -i ~/.ssh/id_rsa.pub root@192.168.1.10
ssh-copy-id -i ~/.ssh/id_rsa.pub root@192.168.1.12

##登录k8s-node2
ssh-keygen -t rsa
ssh-copy-id -i ~/.ssh/id_rsa.pub root@192.168.1.10
ssh-copy-id -i ~/.ssh/id_rsa.pub root@192.168.1.11

##安装Docker
```
$ wget https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo -O /etc/yum.repos.d/docker-ce.repo
$ yum -y install docker-ce-18.06.1.ce-3.el7
$ systemctl enable docker && systemctl start docker
$ docker --version
Docker version 18.06.1-ce, build e68fc7a
```
##镜像加速
```
$ cat >/etc/docker/daemon.json <<EOF
{
  "registry-mirrors": ["https://yrg1jgp2.mirror.aliyuncs.com"]
}
EOF
```
###  添加阿里云YUM软件源
```
$ cat > /etc/yum.repos.d/kubernetes.repo << EOF
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=0
repo_gpgcheck=0
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF
```
  
###  yum 安装Docker
```
$ wget https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo -O /etc/yum.repos.d/docker-ce.repo
$ yum -y install docker-ce-18.06.1.ce-3.el7
$ systemctl daemon-reload && systemctl enable docker && systemctl start docker
$ systemctl restart docker
$ docker --version
Docker version 18.06.1-ce, build e68fc7a
```

##配置docker镜像加速器
#https://cr.console.aliyun.com/cn-hangzhou/instances/mirrors?userCode=9isywqm2&share_source=copy_link
$ cat > /etc/docker/daemon.json << EOF
{
   "registry-mirrors": [
        "https://yrg1jgp2.mirror.aliyuncs.com",
        "https://registry.cn-hangzhou.aliyuncs.com",
        "https://mirror.baidubce.com",
        "https://mirror.ccs.tencentyun.com",
        "https://hub-mirror.c.163.com",
        "https://registry.docker-cn.com"
    ],
    "insecure-registries": [
        "10.199.210.66:5000",
        "registry.cn-hangzhou.aliyuncs.com"
    ]
}
EOF
docker info | grep "Cgroup Driver"
###  安装kubeadm，kubelet和kubectl
```
$ yum install -y kubelet-1.18.0 kubeadm-1.18.0 kubectl-1.18.0
$ systemctl daemon-reload && systemctl enable kubelet && systemctl start kubelet
$ systemctl restart kubelet
$ systemctl status kubelet
```
## kubelet启动失败 校验 Cgroup 
docker info|grep Driver
systemctl show --property=Environment kubelet | cat
#修改kubelet的Cgroup Driver
修改/etc/systemd/system/kubelet.service.d/10-kubeadm.conf或/usr/lib/systemd/system/kubelet.service.d/10-kubeadm.conf 文件，
增加–cgroup-driver=systemd (官方推荐用systemd)
   
## /var/lib/kubelet/config.yaml   
```yaml
apiVersion: kubelet.config.k8s.io/v1beta1
kind: KubeletConfiguration
cgroupDriver: systemd
```
   
   
```
kubeadm config images list
vim pull_images.sh
#!/bin/bash
images=(
k8s.gcr.io/kube-apiserver:v1.18.0
k8s.gcr.io/kube-controller-manager:v1.18.0
k8s.gcr.io/kube-scheduler:v1.18.0
k8s.gcr.io/kube-proxy:v1.18.0
k8s.gcr.io/pause:3.2
k8s.gcr.io/etcd:3.4.3-0
k8s.gcr.io/coredns:1.6.7
)
for imageName in ${images[@]}; do
key=`echo $imageName | awk -F '\\\/' '{print $3}' | awk -F ':' '{print $1}'`
docker save -o $key.tar $imageName
done
```

##  部署Kubernetes Master  
```
docker login --username=大林小莉2021 registry.cn-hangzhou.aliyuncs.com #Paul省份

kubeadm init \
  --apiserver-advertise-address=192.168.1.10 \
  --image-repository registry.aliyuncs.com/google_containers \
  --kubernetes-version v1.18.0 \
  --service-cidr=10.96.0.0/12 \
  --pod-network-cidr=10.244.0.0/16 \
  --ignore-preflight-errors=all \
  --token-ttl 0

kubeadm token create --ttl 0 #永久token生成
```
##yaml组件位置 /etc/kubernetes/manifests
etcd.yaml  kube-apiserver.yaml  kube-controller-manager.yaml  kube-scheduler.yaml

##使用kubectl工具：所有机器执行拷贝 保存到当前用户的.kube 目录下，kubectl 默认会使用这个目录下的授权信息访问 Kubernetes 集群。
##如果不这么做的话，我们每次都需要通过 export KUBECONFIG 环境变量告诉 kubectl 这个安全配置文件的位置
```bash
mkdir -p /root/.kube
sudo cp -i /etc/kubernetes/admin.conf /root/.kube/config
sudo chown $(id -u):$(id -g) /root/.kube/config
$ kubectl get nodes
NAME     STATUS     ROLES    AGE   VERSION
k8s-m1   NotReady   master   47m   v1.18.0
注：由于网络插件还没有部署，还没有准备就绪 NotReady，继续操作。 
```
## kubeadm token node集群其它机器,join后kubelet才能启动
```shell script
kubeadm join 192.168.1.10:6443 --token xv1c3z.n5u9btwjwvtvogi8 \
    --discovery-token-ca-cert-hash sha256:df4fdc148facfba046df7dca895fb0c4a389e330348e97ab448f1e1ef3235b38
```
##部署CNI网络插件使用的是一个名叫 CNI 的通用接口，它也是当前容器网络的事实标准，市面上的所有容器网络开源项目都可以通过 CNI 接入 Kubernetes，比如 Flannel、Calico、Canal、Romana 等等，它们的部署方式也都是类似的“一键部署”
```
wget  https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
kubectl apply -f kube-flannel.yml
kubectl get pods -n kube-system
NAME                          READY   STATUS    RESTARTS   AGE
kube-flannel-ds-amd64-2pc95   1/1     Running   0          72s
```
##Volume持久卷 Kubernetes 本身的松耦合设计，绝大多数存储项目，比如 Ceph、GlusterFS、NFS 等，都可以为 Kubernetes 提供持久化存储能力。在这次的部署实战中，我会选择部署一个很重要的 Kubernetes 存储插件项目：Rook。

##命令
kubectl get cs
kubectl get nodes
scp -r  /root/.kube/config root@192.168.1.11:/root/.kube/
scp -r  /root/.kube/config root@192.168.1.12:/root/.kube/
##k8s-n1 not ready 用 kubectl describe node master 或  kubectl get pods -n kube-system

kubectl describe node k8s-n1
##Apr 27 06:27:36 host kubelet: F0427 06:27:36.540622    8344 kubelet.go:1384] Failed to start ContainerManager failed to initialize top level QOS containers: failed to update top level Burstable QOS cgroup : failed to set supported cgroup subsystems for cgroup [kubepods burstable]: failed to find subsystem mount for required subsystem: pids
vi /usr/lib/systemd/system/kubelet.service.d/10-kubeadm.conf
在末尾ExecStart上添加 --feature-gates SupportPodPidsLimit=false --feature-gates SupportNodePidsLimit=false
$ systemctl daemon-reload && systemctl restart kubelet
##申请请求证书
$ kubectl get csr 
## k8s-n1 error: no configuration has been provided, try setting KUBERNETES_MASTER environment variable 
##Config not found: /etc/kubernetes/admin.conf
kubectl get nodes --kubeconfig /etc/kubernetes/kubelet.conf
kubectl apply -f kube-flannel.yml --kubeconfig /etc/kubernetes/kubelet.conf
#配置镜像下载加速器：
vim /etc/docker/daemon.json

{
  "registry-mirrors": ["https://yrg1jgp2.mirror.aliyuncs.com"],
  "exec-opts": ["native.cgroupdriver=systemd"]
}

systemctl restart docker
systemctl restart kubelet

##kubelet启动错误 journalctl -xefu kubelet  日志不明显 手动启动 /usr/bin/kubelet
## error: no configuration has been provided, try setting KUBERNETES_MASTER environment variable
export KUBECONFIG=/etc/kubernetes/admin.conf
source /etc/profile
##[WARNING IsDockerSystemdCheck]: detected "cgroupfs" as the Docker cgroup driver. 与 exec-opts
vim /var/lib/kubelet/kubeadm-flags.env
KUBELET_KUBEADM_ARGS="--cgroup-driver=cgroupfs --network-plugin=cni --pod-infra-container-image=registry.aliyuncs.com/google_containers/pause:3.2"
KUBELET_KUBEADM_ARGS="--cgroup-driver=systemd --network-plugin=cni --pod-infra-container-image=registry.aliyuncs.com/google_containers/pause:3.2"
##KUBELET_EXTRA_ARGS 在标志链中排在最后，并且在设置冲突时具有最高优先级
/etc/sysconfig/kubelet KUBELET_EXTRA_ARGS 
--feature-gates SupportPodPidsLimit=false --feature-gates SupportNodePidsLimit=false
$KUBELET_KUBECONFIG_ARGS $KUBELET_CONFIG_ARGS $KUBELET_KUBEADM_ARGS $KUBELET_EXTRA_ARGS
/usr/lib/systemd/system/kubelet.service.d/10-kubeadm.conf     
##docker登录私库时提示 x509: certificate signed by unknown authority  insecure-registries:registry.cn-hangzhou.aliyuncs.com
#Failed to list *v1beta1.RuntimeClass: 
##Failed to start ContainerManager failed to initialize top level QOS containers: failed to update top level Burstable QOS cgroup : failed to set supported cgroup subsystems for cgroup [kubepods burstable]: failed to find subsystem mount for required subsystem: pids
解决方法：
```shell script
for i in $(systemctl list-unit-files --no-legend --no-pager -l | grep --color=never -o .*.slice | grep kubepod);
do systemctl stop $i;
done
```
systemctl stop kubepods.slice后，再重启kubelet   systemctl restart kubelet
systemctl status kubelet
##解决：kubeadm reset 
```shell script
kubeadm reset -f
rm -rf /etc/cni /etc/kubernetes /var/lib/dockershim /var/lib/etcd /var/lib/kubelet /var/run/kubernetes ~/.kube/*  rm /etc/cni/net.d/*

iptables -F && iptables -X
iptables -t nat -F && iptables -t nat -X
iptables -t raw -F && iptables -t raw -X
iptables -t mangle -F && iptables -t mangle -X
```  


#docker pull registry.cn-hangzhou.aliyuncs.com/google_containers/kube-apiserver:v1.18.0

##CentOS7系统时间与真实时间相差8小时
timedatectl set-timezone Asia/Shanghai


##kubeadm 离线安装
kubeadm config images list
```text
k8s.gcr.io/kube-apiserver:v1.18.0
k8s.gcr.io/kube-controller-manager:v1.18.0
k8s.gcr.io/kube-scheduler:v1.18.0
k8s.gcr.io/kube-proxy:v1.18.0
k8s.gcr.io/pause:3.2
k8s.gcr.io/etcd:3.4.3-0
k8s.gcr.io/coredns:1.6.7
```
## pull_images.sh 能联网机器上
```shell script
#!/bin/bash
images=(
registry.aliyuncs.com/google_containers/kube-apiserver:v1.18.0
registry.aliyuncs.com/google_containers/kube-controller-manager:v1.18.
registry.aliyuncs.com/google_containers/kube-scheduler:v1.18.0
registry.aliyuncs.com/google_containers/kube-proxy:v1.18.0
registry.aliyuncs.com/google_containers/pause:3.2
registry.aliyuncs.com/google_containers/etcd:3.4.3-0
registry.aliyuncs.com/google_containers/coredns:1.6.7
)
for pullimageName in ${images[@]} ; do
docker pull $pullimageName
done
```
 
## save_images.sh 镜像导出tar 
```shell script
#!/bin/bash
images=(
registry.aliyuncs.com/google_containers/kube-apiserver:v1.18.0
registry.aliyuncs.com/google_containers/kube-controller-manager:v1.18.
registry.aliyuncs.com/google_containers/kube-scheduler:v1.18.0
registry.aliyuncs.com/google_containers/kube-proxy:v1.18.0
registry.aliyuncs.com/google_containers/pause:3.2
registry.aliyuncs.com/google_containers/etcd:3.4.3-0
registry.aliyuncs.com/google_containers/coredns:1.6.7
)
for imageName in ${images[@]}; do
key=`echo $imageName | awk -F '\\\/' '{print $3}' | awk -F ':' '{print $1}'`
docker save -o $key.tar $imageName
done
```
## load_images.sh 拷贝 master节点导入本地镜像 
# /bin/bash^M: bad interpreter: No such file or directory” set ff=unix
```shell script
#!/bin/bash
images=(
kube-apiserver
kube-controller-manager
kube-scheduler
kube-proxy
pause
etcd
coredns
)
for imageName in ${images[@]} ; do
key=.tar
docker load -i $imageName$key
done
```
