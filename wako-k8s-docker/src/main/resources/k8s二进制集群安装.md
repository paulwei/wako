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
10.199.215.12 k8s_master
10.199.215.181 k8s_node1
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

## 准备cfssl证书生成工具
d ~
yum install wget -y
wget https://pkg.cfssl.org/R1.2/cfssl_linux-amd64
wget https://pkg.cfssl.org/R1.2/cfssljson_linux-amd64
wget https://pkg.cfssl.org/R1.2/cfssl-certinfo_linux-amd64

chmod +x cfssl_linux-amd64 cfssljson_linux-amd64 cfssl-certinfo_linux-amd64

cp cfssl_linux-amd64 /usr/local/bin/cfssl
cp cfssljson_linux-amd64 /usr/local/bin/cfssljson
cp cfssl-certinfo_linux-amd64 /usr/local/bin/cfssl-certinfo

##自签证书颁发机构（CA）
mkdir -p ~/TLS/{etcd,k8s}
cd ~/TLS/etcd
cat >ca-config.json <<EOF
{
  "signing": {
    "default": {
      "expiry": "87600h"
    },
    "profiles": {
      "www": {
        "expiry": "87600h",
        "usages": [
          "signing",
          "key encipherment",
          "server auth",
          "client auth"
        ]
      }
    }
  }
}
EOF
cat >ca-csr.json <<EOF
{
  "CN": "etcd CA",
    "key": {
      "algo": "rsa",
      "size": 2048
    },
    "names": [
      {
        "C": "CN",
        "L": "Shanghai"
      }
    ]
}
EOF
cfssl gencert -initca ca-csr.json | cfssljson -bare ca -
cat >server-csr.json <<EOF
{
  "CN": "etcd",
  "hosts": [
    "10.199.215.12",
    "10.199.215.13",
    "10.199.215.14",
    "10.199.215.181",
    "10.199.215.183",
    "10.199.215.184",
    "10.199.210.66",
    "10.107.98.255",
    "10.107.72.150"
  ],
  "key": {
    "algo": "rsa",
    "size": 2048
  },
  "names": [
    {
      "C": "CN",
      "L": "Shanghai",
      "ST": "Shanghai"
    }
  ]
}
EOF
##证书
cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=www server-csr.json | cfssljson -bare server

##etcd安装k8s-master
mkdir /opt/etcd/{bin,cfg,ssl} -p
tar zxvf etcd-v3.4.9-linux-amd64.tar.gz
mv etcd-v3.4.9-linux-amd64/{etcd,etcdctl} /opt/etcd/bin/

##k8s-master
cat >/opt/etcd/cfg/etcd.conf <<EOF
#[Member]
ETCD_NAME="etcd-1"
ETCD_DATA_DIR="/var/lib/etcd/default.etcd"
ETCD_LISTEN_PEER_URLS="https://10.199.215.12:2380"
ETCD_LISTEN_CLIENT_URLS="https://10.199.215.12:2379"
#[Clustering]
ETCD_INITIAL_ADVERTISE_PEER_URLS="https://10.199.215.12:2380"
ETCD_ADVERTISE_CLIENT_URLS="https://10.199.215.12:2379"
ETCD_INITIAL_CLUSTER="etcd-1=https://10.199.215.12:2380,etcd-2=https://10.199.215.181:2380"
ETCD_INITIAL_CLUSTER_TOKEN="etcd-cluster"
ETCD_INITIAL_CLUSTER_STATE="new"
EOF

##k8s-node1
cat >/opt/etcd/cfg/etcd.conf <<EOF
#[Member]
ETCD_NAME="etcd-2"
ETCD_DATA_DIR="/var/lib/etcd/default.etcd"
ETCD_LISTEN_PEER_URLS="https://10.199.215.181:2380"
ETCD_LISTEN_CLIENT_URLS="https://10.199.215.181:2379"
#[Clustering]
ETCD_INITIAL_ADVERTISE_PEER_URLS="https://10.199.215.181:2380"
ETCD_ADVERTISE_CLIENT_URLS="https://10.199.215.181:2379"
ETCD_INITIAL_CLUSTER="etcd-1=https://10.199.215.12:2380,etcd-2=https://10.199.215.181:2380"
ETCD_INITIAL_CLUSTER_TOKEN="etcd-cluster"
ETCD_INITIAL_CLUSTER_STATE="new"
EOF

##systemd管理etcd
cat >/usr/lib/systemd/system/etcd.service <<EOF
[Unit]
Description=Etcd Server
After=network.target
After=network-online.target
Wants=network-online.target
[Service]
Type=notify
EnvironmentFile=/opt/etcd/cfg/etcd.conf
ExecStart=/opt/etcd/bin/etcd \
--cert-file=/opt/etcd/ssl/server.pem \
--key-file=/opt/etcd/ssl/server-key.pem \
--peer-cert-file=/opt/etcd/ssl/server.pem \
--peer-key-file=/opt/etcd/ssl/server-key.pem \
--trusted-ca-file=/opt/etcd/ssl/ca.pem \
--peer-trusted-ca-file=/opt/etcd/ssl/ca.pem \
--logger=zap
Restart=on-failure
LimitNOFILE=65536

[Install]
WantedBy=multi-user.target
EOF

##群内节点SSH登录免密
##登录k8s-master
ssh-keygen -t rsa
ssh-copy-id -i ~/.ssh/id_rsa.pub root@10.199.215.181
##登录k8s-node1
ssh-keygen -t rsa
ssh-copy-id -i ~/.ssh/id_rsa.pub root@10.199.215.12

##拷贝刚才生成的证书
cp ~/TLS/etcd/ca*pem ~/TLS/etcd/server*pem /opt/etcd/ssl/
##将上面节点1所有的生成文件拷贝到节点2
scp -r /opt/etcd/ root@k8s_node1:/opt/
scp /usr/lib/systemd/system/etcd.service root@k8s_node1:/usr/lib/systemd/system/
scp -r /opt/etcd/ root@k8s_node1:/opt/

##System.d启动etcd
systemctl daemon-reload
systemctl start etcd
systemctl enable etcd

##启动失败检查 
systemctl status firewalld.service
##忘记
1) cp ~/TLS/etcd/ca*pem ~/TLS/etcd/server*pem /opt/etcd/ssl/
2) 检查/opt/etcd/cfg/etcd.conf主从不同
3) 同时启动集群


##查看集群状态
/opt/etcd/bin/etcdctl --cacert=/opt/etcd/ssl/ca.pem --cert=/opt/etcd/ssl/server.pem --key=/opt/etcd/ssl/server-key.pem --endpoints="https://10.199.215.12:2379,https://10.199.215.181:2379" endpoint health --write-out=table

## 手动 docker 安装
1) 先删除已安装不匹配docker版本
yum list installed | grep docker
rpm -qa|grep docker
rm -rf /var/lib/docker

tar zxvf docker-18.06.1-ce.tgz
chown root:root docker -R
mv docker/* /usr/bin
systemctl enable docker && systemctl start docker

###  yum 安装Docker
```
$ wget https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo -O /etc/yum.repos.d/docker-ce.repo
$ yum -y install docker-ce-18.06.1.ce-3.el7
$ systemctl daemon-reload && systemctl enable docker && systemctl start docker
$ docker --version
Docker version 18.06.1-ce, build e68fc7a
```

##配置docker镜像加速器
#https://cr.console.aliyun.com/cn-hangzhou/instances/mirrors?userCode=9isywqm2&share_source=copy_link
$ cat > /etc/docker/daemon.json << EOF
{
  "registry-mirrors": ["https://yrg1jgp2.mirror.aliyuncs.com"]
}
EOF

## k8s-master 生成kube-apiserver证书
cat > ca-config.json << EOF
{
  "signing": {
    "default": {
      "expiry": "87600h"
    },
    "profiles": {
      "kubernetes": {
         "expiry": "87600h",
         "usages": [
            "signing",
            "key encipherment",
            "server auth",
            "client auth"
        ]
      }
    }
  }
}
EOF

cat > ca-csr.json << EOF
{
    "CN": "kubernetes",
    "key": {
        "algo": "rsa",
        "size": 2048
    },
    "names": [
        {
            "C": "CN",
            "L": "Shanghai",
            "ST": "Shanghai",
            "O": "k8s",
            "OU": "System"
        }
    ]
}
EOF

##kube-apiserver证书
cfssl gencert -initca ca-csr.json | cfssljson -bare ca -

  
##使用自签CA签发kube-apiserver HTTPS证书  hosts字段中IP为所有Master/LB/VIP IP，一个都不能少！为了方便后期扩容可以多写几个预留的IP
cat >server-csr.json <<EOF
 {
     "CN": "kubernetes",
     "hosts": [
       "10.0.0.1",
       "127.0.0.1",
       "10.199.215.12",
       "10.199.215.13",
       "10.199.215.14",
       "10.199.215.181",
       "10.199.215.183",
       "10.199.215.184",
       "10.199.210.66",
       "10.107.98.255",
       "10.107.72.150",
       "kubernetes",
       "kubernetes.default",
       "kubernetes.default.svc",
       "kubernetes.default.svc.cluster",
       "kubernetes.default.svc.cluster.local"
     ],
     "key": {
         "algo": "rsa",
         "size": 2048
     },
     "names": [
         {
             "C": "CN",
             "L": "Shanghai",
             "ST": "Shanghai",
             "O": "k8s",
             "OU": "System"
         }
     ]
 }
EOF
##HTTPS证书
cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=kubernetes server-csr.json | cfssljson -bare server
##拷贝证书
cp ~/TLS/k8s/ca*pem ~/TLS/k8s/server*pem /opt/kubernetes/ssl/

##部署kube-apiserver
mkdir -p /opt/kubernetes/{bin,cfg,ssl,logs} 
tar zxvf kubernetes-server-linux-amd64.tar.gz
cd kubernetes/server/bin
cp kube-apiserver kube-scheduler kube-controller-manager /opt/kubernetes/bin
cp kubectl /usr/bin/
##kube-apiserver conf
cat > /opt/kubernetes/cfg/kube-apiserver.conf << EOF
KUBE_APISERVER_OPTS="--logtostderr=false \\
--v=2 \\
--log-dir=/opt/kubernetes/logs \\
--etcd-servers=https://10.199.215.12:2379,https://10.199.215.181:2379 \\
--bind-address=10.199.215.12 \\
--secure-port=6443 \\
--advertise-address=10.199.215.12 \\
--allow-privileged=true \\
--service-cluster-ip-range=10.0.0.0/24 \\
--enable-admission-plugins=NamespaceLifecycle,LimitRanger,ServiceAccount,ResourceQuota,NodeRestriction \\
--authorization-mode=RBAC,Node \\
--enable-bootstrap-token-auth=true \\
--token-auth-file=/opt/kubernetes/cfg/token.csv \\
--service-node-port-range=30000-32767 \\
--kubelet-client-certificate=/opt/kubernetes/ssl/server.pem \\
--kubelet-client-key=/opt/kubernetes/ssl/server-key.pem \\
--tls-cert-file=/opt/kubernetes/ssl/server.pem  \\
--tls-private-key-file=/opt/kubernetes/ssl/server-key.pem \\
--client-ca-file=/opt/kubernetes/ssl/ca.pem \\
--service-account-key-file=/opt/kubernetes/ssl/ca-key.pem \\
--etcd-cafile=/opt/etcd/ssl/ca.pem \\
--etcd-certfile=/opt/etcd/ssl/server.pem \\
--etcd-keyfile=/opt/etcd/ssl/server-key.pem \\
--audit-log-maxage=30 \\
--audit-log-maxbackup=3 \\
--audit-log-maxsize=100 \\
--audit-log-path=/opt/kubernetes/logs/k8s-audit.log"
EOF

##启用 TLS Bootstrapping 机制

##自行生成token[4174193d23ac62f24f1a0e43e713e5aa] 格式：token，用户名，UID，用户组
head -c 16 /dev/urandom | od -An -t x | tr -d ' '
cat > /opt/kubernetes/cfg/token.csv << EOF
4174193d23ac62f24f1a0e43e713e5aa,kubelet-bootstrap,10001,"system:kubelet-bootstrap"
EOF
##systemd管理apiserver
```shell script
cat > /usr/lib/systemd/system/kube-apiserver.service << EOF
[Unit]
Description=Kubernetes API Server
Documentation=https://github.com/kubernetes/kubernetes
[Service]
EnvironmentFile=/opt/kubernetes/cfg/kube-apiserver.conf
ExecStart=/opt/kubernetes/bin/kube-apiserver \$KUBE_APISERVER_OPTS
Restart=on-failure
[Install]
WantedBy=multi-user.target
EOF
##启动
systemctl daemon-reload && systemctl start kube-apiserver && systemctl enable kube-apiserver
 
```
##授权kubelet-bootstrap用户允许请求证书
```shell script
kubectl create clusterrolebinding kubelet-bootstrap \
--clusterrole=system:node-bootstrapper \
--user=kubelet-bootstrap
```
##部署kube-controller-manager创建配置文件
###–master：通过本地非安全本地端口8080连接apiserver。
###–leader-elect：当该组件启动多个时，自动选举（HA）
###cluster-signing-cert-file/–cluster-signing-key-file：自动为kubelet颁发证书的CA，与apiserver保持一致
```shell script
cat > /opt/kubernetes/cfg/kube-controller-manager.conf << EOF
KUBE_CONTROLLER_MANAGER_OPTS="--logtostderr=false \\
--v=2 \\
--log-dir=/opt/kubernetes/logs \\
--leader-elect=true \\
--master=127.0.0.1:8080 \\
--bind-address=127.0.0.1 \\
--allocate-node-cidrs=true \\
--cluster-cidr=10.244.0.0/16 \\
--service-cluster-ip-range=10.0.0.0/24 \\
--cluster-signing-cert-file=/opt/kubernetes/ssl/ca.pem \\
--cluster-signing-key-file=/opt/kubernetes/ssl/ca-key.pem  \\
--root-ca-file=/opt/kubernetes/ssl/ca.pem \\
--service-account-private-key-file=/opt/kubernetes/ssl/ca-key.pem \\
--experimental-cluster-signing-duration=87600h0m0s"
EOF
```
##systemd管理controller-manager
```shell script
cat > /usr/lib/systemd/system/kube-controller-manager.service << EOF
[Unit]
Description=Kubernetes Controller Manager
Documentation=https://github.com/kubernetes/kubernetes
[Service]
EnvironmentFile=/opt/kubernetes/cfg/kube-controller-manager.conf
ExecStart=/opt/kubernetes/bin/kube-controller-manager \$KUBE_CONTROLLER_MANAGER_OPTS
Restart=on-failure
[Install]
WantedBy=multi-user.target
EOF
##启动
systemctl daemon-reload && systemctl start kube-controller-manager && systemctl enable kube-controller-manager
```
##部署kube-scheduler
```shell script
cat > /opt/kubernetes/cfg/kube-scheduler.conf << EOF
KUBE_SCHEDULER_OPTS="--logtostderr=false \
--v=2 \
--log-dir=/opt/kubernetes/logs \
--leader-elect \
--master=127.0.0.1:8080 \
--bind-address=127.0.0.1"
EOF
```
##systemd管理scheduler
```shell script
cat > /usr/lib/systemd/system/kube-scheduler.service << EOF
[Unit]
Description=Kubernetes Scheduler
Documentation=https://github.com/kubernetes/kubernetes
[Service]
EnvironmentFile=/opt/kubernetes/cfg/kube-scheduler.conf
ExecStart=/opt/kubernetes/bin/kube-scheduler \$KUBE_SCHEDULER_OPTS
Restart=on-failure
[Install]
WantedBy=multi-user.target
EOF
##启动
systemctl daemon-reload && systemctl start kube-scheduler && systemctl enable kube-scheduler
```
##部署kubectl 生成kubectl连接集群的证书
```shell script
cat > ~/TLS/k8s/admin-csr.json <<EOF
{
  "CN": "admin",
  "hosts": [],
  "key": {
    "algo": "rsa",
    "size": 2048
  },
  "names": [
    {
      "C": "CN",
      "L": "Shanghai",
      "ST": "Shanghai",
      "O": "system:masters",
      "OU": "System"
    }
  ]
}
EOF
```
##生产证书
```shell script
cd ~/TLS/k8s/
cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=kubernetes admin-csr.json | cfssljson -bare admin
```
##生成kubeconfig文件
```shell script
mkdir /root/.kube
cd ~/TLS/k8s/

KUBE_CONFIG="/root/.kube/config"
KUBE_APISERVER="https://10.199.215.12:6443"

kubectl config set-cluster kubernetes \
  --certificate-authority=/opt/kubernetes/ssl/ca.pem \
  --embed-certs=true \
  --server=${KUBE_APISERVER} \
  --kubeconfig=${KUBE_CONFIG}
kubectl config set-credentials cluster-admin \
  --client-certificate=./admin.pem \
  --client-key=./admin-key.pem \
  --embed-certs=true \
  --kubeconfig=${KUBE_CONFIG}
kubectl config set-context default \
  --cluster=kubernetes \
  --user=cluster-admin \
  --kubeconfig=${KUBE_CONFIG}
kubectl config use-context default --kubeconfig=${KUBE_CONFIG}
```
##查看当前集群组件状态
kubectl get cs
  
##部署Worker Node同时master
###创建工作目录并拷贝二进制文件
```shell script
cd ~
mkdir -p /opt/kubernetes/{bin,cfg,ssl,logs}
cd kubernetes/server/bin
cp kubelet kube-proxy /opt/kubernetes/bin
```
##部署kubelet
```shell script
cat > /opt/kubernetes/cfg/kubelet.conf << EOF
KUBELET_OPTS="--logtostderr=false \\
--v=2 \\
--log-dir=/opt/kubernetes/logs \\
--hostname-override=k8s_master \\  # 特别主要地方
--network-plugin=cni \\
--kubeconfig=/opt/kubernetes/cfg/kubelet.kubeconfig \\
--bootstrap-kubeconfig=/opt/kubernetes/cfg/bootstrap.kubeconfig \\
--config=/opt/kubernetes/cfg/kubelet-config.yml \\
--cert-dir=/opt/kubernetes/ssl \\
--pod-infra-container-image=lizhenliang/pause-amd64:3.0"
EOF
```
–hostname-override：显示名称，集群中唯一
–network-plugin：启用CNI
–kubeconfig：空路径，会自动生成，后面用于连接apiserver
–bootstrap-kubeconfig：首次启动向apiserver申请证书
–config：配置参数文件
–cert-dir：kubelet证书生成目录
–pod-infra-container-image：管理Pod网络容器的镜像

```shell script
cat > /opt/kubernetes/cfg/kubelet-config.yml << EOF
kind: KubeletConfiguration
apiVersion: kubelet.config.k8s.io/v1beta1
address: 0.0.0.0
port: 10250
readOnlyPort: 10255
cgroupDriver: cgroupfs
clusterDNS:
- 10.0.0.2
clusterDomain: cluster.local 
failSwapOn: false
authentication:
  anonymous:
    enabled: false
  webhook:
    cacheTTL: 2m0s
    enabled: true
  x509:
    clientCAFile: /opt/kubernetes/ssl/ca.pem 
authorization:
  mode: Webhook
  webhook:
    cacheAuthorizedTTL: 5m0s
    cacheUnauthorizedTTL: 30s
evictionHard:
  imagefs.available: 15%
  memory.available: 100Mi
  nodefs.available: 10%
  nodefs.inodesFree: 5%
maxOpenFiles: 1000000
maxPods: 110
EOF``
```
##生成bootstrap.kubeconfig文件

```shell script
cd ~
KUBE_APISERVER="https://10.199.215.12:6443" # apiserver IP:PORT
TOKEN="4174193d23ac62f24f1a0e43e713e5aa" # 与token.csv里保持一致

# 生成 kubelet bootstrap kubeconfig 配置文件
kubectl config set-cluster kubernetes \
  --certificate-authority=/opt/kubernetes/ssl/ca.pem \
  --embed-certs=true \
  --server=${KUBE_APISERVER} \
  --kubeconfig=bootstrap.kubeconfig
kubectl config set-credentials "kubelet-bootstrap" \
  --token=${TOKEN} \
  --kubeconfig=bootstrap.kubeconfig
kubectl config set-context default \
  --cluster=kubernetes \
  --user="kubelet-bootstrap" \
  --kubeconfig=bootstrap.kubeconfig
kubectl config use-context default --kubeconfig=bootstrap.kubeconfig
cp bootstrap.kubeconfig /opt/kubernetes/cfg
```
##systemd管理kubelet
```shell script
cat > /usr/lib/systemd/system/kubelet.service << EOF
[Unit]
Description=Kubernetes Kubelet
After=docker.service
[Service]
EnvironmentFile=/opt/kubernetes/cfg/kubelet.conf
ExecStart=/opt/kubernetes/bin/kubelet \$KUBELET_OPTS
Restart=on-failure
LimitNOFILE=65536
[Install]
WantedBy=multi-user.target
EOF
systemctl daemon-reload && systemctl start kubelet && systemctl enable kubelet
systemctl status kubelet
```
```shell script
systemctl daemon-reload
systemctl start kube-apiserver
systemctl start kube-controller-manager
systemctl start kube-scheduler
systemctl start kubelet
systemctl start kube-proxy
systemctl enable kube-apiserver
systemctl enable kube-controller-manager
systemctl enable kube-scheduler
systemctl enable kubelet
systemctl enable kube-proxy
```
##批准kubelet证书申请并加入集群
