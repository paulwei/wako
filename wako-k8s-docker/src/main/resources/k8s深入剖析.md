## Namespace 做隔离[pid障眼法]，Cgroups 做限制，rootfs 做文件系统

## 容器的本质到底是什么？
容器的本质是进程，容器镜像就是这个系统里的“.exe”安装包。Kubernetes 就是操作系统

## IP网段表示法
　　 举例说明：192.168.0.0/24 192.168.0.0: 网络地址 24: 表示子网掩码二进制表示法中，连续的 1 的 个数，这里为：11111111·11111111·11111111·00000000，即 255.255.255.0

## Pod本质？
Pod：其实是一组共享了某些资源的容器。像 imklog、imuxsock 和 main 函数主进程这样的三个容器，正是一个典型的由三个容器组成的 Pod。
Kubernetes 项目在调度时，自然就会去选择可用内存等于 3 GB 的 node-1 节点进行绑定，而根本不会考虑 node-2
Pod 看成传统环境里的“机器”、把容器看作是运行在这个“机器”里的“用户程序”,比如，凡是调度、网络、存储，以及安全相关的属性，基本上是 Pod 级别的

## pod容器创建
infra[基础容器]->A[用户容器]
Kubernetes 项目里，Pod 的实现需要使用一个中间容器，这个容器叫作 Infra 容器。在这个 Pod 中，Infra 容器永远都是第一个被创建的容器，而其他用户定义的容器，则通过 Join Network Namespace 的方式，与 Infra 容器关联在一起。
Infra 容器一定要占用极少的资源，所以它使用的是一个非常特殊的镜像，叫作：k8s.gcr.io/pause。这个镜像是一个用汇编语言编写的、永远处于“暂停”状态的容器，解压后的大小也只有 100~200 KB 左右。
Pod这几种类型的容器：
•Infrastructure Container：基础容器
  •维护整个Pod网络空间
•InitContainers：初始化容器
  •先于业务容器开始执行
•Containers：业务容器
  •并行启动

## pod容器启动顺序
Pod 中，所有 Init Container 定义的容器，都会比 spec.containers 定义的用户容器先启动。并且，Init Container 容器会按顺序逐一启动，而直到它们都启动并且退出了，用户容器才会启动
sidecar 指的就是我们可以在一个 Pod 中，启动一个辅助容器，来完成一些独立于主进程（主容器）之外的工作
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: javaweb-2
spec:
  initContainers:
  - image: geektime/sample:v2
    name: war
    command: ["cp", "/sample.war", "/app"]
    volumeMounts:
    - mountPath: /app
      name: app-volume
  containers:
  - image: geektime/tomcat:7.0
    name: tomcat
    command: ["sh","-c","/root/apache-tomcat-7.0.42-v2/bin/start.sh"]
    volumeMounts:
    - mountPath: /root/apache-tomcat-7.0.42-v2/webapps
      name: app-volume
    ports:
    - containerPort: 8080
      hostPort: 8001 
  volumes:
  - name: app-volume
    emptyDir: {}
```
## Pod 生命周期
Pending。这个状态意味着，Pod 的 YAML 文件已经提交给了 Kubernetes，API 对象已经被创建并保存在 Etcd 当中。但是，这个 Pod 里有些容器因为某种原因而不能被顺利创建。比如，调度不成功。
Running。这个状态下，Pod 已经调度成功，跟一个具体的节点绑定。它包含的容器都已经创建成功，并且至少有一个正在运行中。
Succeeded。这个状态意味着，Pod 里的所有容器都正常运行完毕，并且已经退出了。这种情况在运行一次性任务时最为常见。
Failed。这个状态下，Pod 里至少有一个容器以不正常的状态（非 0 的返回码）退出。这个状态的出现，意味着你得想办法 Debug 这个容器的应用，比如查看 Pod 的 Events 和日志。
Unknown。这是一个异常状态，意味着 Pod 的状态不能持续地被 kubelet 汇报给 kube-apiserver，这很有可能是主从节点（Master 和 Kubelet）间的通信出现了问题。

## Kubernetes 支持的 Projected Volume 一共有四种：
Secret；
ConfigMap；
Downward API；
ServiceAccountToken。

## Pod 恢复机制 pod.spec.restartPolicy 默认值是 Always
Always：在任何情况下，只要容器不在运行状态，就自动重启容器；
OnFailure: 只在容器 异常时才自动重启容器；
Never: 从来不重启容器。

## pod控制器 Deployment
kubelet 通过心跳汇报的容器状态和节点状态，或者监控系统中保存的应用监控数据，或者控制器主动收集的它自己感兴趣的信息，这些都是常见的实际状态的来源。
```shell script
for {
  实际状态 := 获取集群中对象 X 的实际状态（Actual State）
  期望状态 := 获取集群中对象 X 的期望状态（Desired State）
  if 实际状态 == 期望状态{
    什么都不做
  } else {
    执行编排动作，将实际状态调整为期望状态
  }
}
```
Deployment 控制器实际操纵的，正是这样的 ReplicaSet 对象，而不是 Pod 对象。
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
  labels:
    app: nginx
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.7.9
        ports:
        - containerPort: 80
```

## StatefulSet 有状态集合
StatefulSet 其实就是一种特殊的 Deployment，而其独特之处在于，它的每个 Pod 都被编号了。而且，这个编号会体现在 Pod 的名字和 hostname 等标识信息上，这不仅代表了 Pod 的创建顺序，也是 Pod 的重要网络标识（即：在整个集群里唯一的、可被的访问身份）。
首先，StatefulSet 的控制器直接管理的是 Pod。这是因为，StatefulSet 里的不同 Pod 实例，不再像 ReplicaSet 中那样都是完全一样的，而是有了细微区别的。比如，每个 Pod 的 hostname、名字等都是不同的、携带了编号的。而 StatefulSet 区分这些实例的方式，就是通过在 Pod 的名字里加上事先约定好的编号。
其次，Kubernetes 通过 Headless Service，为这些有编号的 Pod，在 DNS 服务器中生成带有同样编号的 DNS 记录。只要 StatefulSet 能够保证这些 Pod 名字里的编号不变，那么 Service 里类似于 web-0.nginx.default.svc.cluster.local 这样的 DNS 记录也就不会变，而这条记录解析出来的 Pod 的 IP 地址，则会随着后端 Pod 的删除和再创建而自动更新。这当然是 Service 机制本身的能力，不需要 StatefulSet 操心。
最后，StatefulSet 还为每一个 Pod 分配并创建一个同样编号的 PVC。这样，Kubernetes 就可以通过 Persistent Volume 机制为这个 PVC 绑定上对应的 PV，从而保证了每一个 Pod 都拥有一个独立的 Volume。
在这种情况下，即使 Pod 被删除，它所对应的 PVC 和 PV 依然会保留下来。
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: web
spec:
  serviceName: "nginx" ##定义service
  replicas: 2
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.9.1
        ports:
        - containerPort: 80
          name: web
```

## Service服务
第一种方式，是以 Service 的 VIP（Virtual IP，即：虚拟 IP）方式。比如：当我访问 10.0.23.1 这个 Service 的 IP 地址时，10.0.23.1 其实就是一个 VIP，它会把请求转发到该 Service 所代理的某一个 Pod 上。这里的具体原理，我会在后续的 Service 章节中进行详细介绍。
第二种方式，就是以 Service 的 DNS 方式。比如：这时候，只要我访问“my-svc.my-namespace.svc.cluster.local”这条 DNS 记录，就可以访问到名叫 my-svc 的 Service 所代理的某一个 Pod。
而在第二种 Service DNS 的方式下，具体还可以分为两种处理方法：
第一种处理方法，是 Normal Service。这种情况下，你访问“my-svc.my-namespace.svc.cluster.local”解析到的，正是 my-svc 这个 Service 的 VIP，后面的流程就跟 VIP 方式一致了。
而第二种处理方法，正是 Headless Service。这种情况下，你访问“my-svc.my-namespace.svc.cluster.local”解析到的，直接就是 my-svc 代理的某一个 Pod 的 IP 地址。可以看到，这里的区别在于，Headless Service 不需要分配一个 VIP，而是可以直接以 DNS 记录的方式解析出被代理 Pod 的 IP 地址。
### Headless Service
```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx
  labels:
    app: nginx
spec:
  ports:
  - port: 80
    name: web
  clusterIP: None
  selector:
    app: nginx
```
Headless Service，其实仍是一个标准 Service 的 YAML 文件。只不过，它的 clusterIP 字段的值是：None，
即：这个 Service，没有一个 VIP 作为“头”。这也就是 Headless 的含义。
所以，这个 Service 被创建后并不会被分配一个 VIP，而是会以 DNS 记录的方式暴露出它所代理的 Pod。
而它所代理的 Pod,Label Selector 机制选择出来的，即：所有携带了 app=nginx 标签的 Pod，都会被这个 Service 代理起来。
<pod-name>.<svc-name>.<namespace>.svc.cluster.local
只要你知道了一个 Pod 的名字，以及它对应的 Service 的名字，你就可以非常确定地通过这条 DNS 记录访问到 Pod 的 IP 地址。

## PV & PVC 持久卷
```yaml
kind: PersistentVolumeClaim
apiVersion: v1
metadata:
  name: pv-claim
spec:
  accessModes:
  - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
```
对于“第一阶段”（Attach），Kubernetes 提供的可用参数是 nodeName，即宿主机的名字。
而对于“第二阶段”（Mount），Kubernetes 提供的可用参数是 dir，即 Volume 的宿主机目录。
Kubernetes 中 PVC 和 PV 的设计，实际上类似于“接口”和“实现”的思想。开发者只要知道并会使用“接口”，即：PVC；而运维人员则负责给“接口”绑定具体的实现，即：PV。
所谓容器的 Volume，其实就是将一个宿主机上的目录，跟一个容器里的目录绑定挂载在了一起
volumeClaimTemplates实现了pvc的自动化，StorageClass实现了pv的自动化
###Local Persistent Volume 共享本地磁盘
用户希望 Kubernetes 能够直接使用宿主机上的本地磁盘目录，而不依赖于远程存储服务，来提供“持久化”的容器 Volume。本地磁盘，尤其是 SSD 盘，它的读写性能相比于大多数远程存储
Kubernetes 在 v1.10 之后，就逐渐依靠 PV、PVC 体系实现了这个特性。这个特性的名字叫作：Local Persistent Volume，Local Persistent Volume 并不适用于所有应用
典型的应用包括：分布式数据存储比如 MongoDB、Cassandra 等，分布式文件系统比如 GlusterFS、Ceph 等，以及需要在本地磁盘上进行大量数据缓存的分布式应用
开始使用 Local Persistent Volume 之前，你首先需要在集群里配置好磁盘或者块设

常用的数据卷：
•节点本地（hostPath，emptyDir）
•网络（NFS，Ceph，GlusterFS）
•公有云（AWS EBS）
•K8S资源（configmap，secret）

## emptyDir vs hostPath
emptyDir卷：是一个临时存储卷，与Pod生命周期绑定一起，如果Pod删除了卷也会被删除
应用场景：Pod中容器之间数据共享
hostPath卷：挂载Node文件系统（Pod所在节点）上文件或者目录到Pod中的容器。
应用场景：Pod中容器需要访问宿主机文件

## 为什么需要StorageClass?  Deploment->PVC->StorageClass[managed-nfs-storage]->Pod[Provisioner]->NFS服务器
1) 在一个大规模的Kubernetes集群里，可能有成千上万个PVC，这就意味着运维人员必须实现创建出这个多个PV，
此外，随着项目的需要，会有新的PVC不断被提交，那么运维人员就需要不断的添加新的，满足要求的PV，否则新的Pod就会因为PVC绑定不到PV而导致创建失败。
而且通过 PVC 请求到一定的存储空间也很有可能不足以满足应用对于存储设备的各种需求。
2) 要使用 StorageClass，我们就得安装对应的自动配置程序，比如我们这里存储后端使用的是 nfs，那么我们就需要使用到一个 nfs-client 的自动配置程序，我们也叫它 Provisioner（制备器），
这个程序使用我们已经配置好的 nfs 服务器，来自动创建持久卷，也就是自动帮我们创建 PV。
K8s默认不支持NFS动态供给，需要单独部署社区开发的插件。
项目地址：https://github.com/kubernetes-sigs/nfs-subdir-external-provisioner 

## ConfigMap vs Secret
ConfigMap：存储配置文件,创建ConfigMap后，数据实际会存储在K8s中Etcd，然后通过创建Pod时引用该数据
Secret：存储敏感数据,所有的数据要经过base64编码,Pod使用Secret数据与ConfigMap方式一样
Pod使用configmap数据有两种方式：
•变量注入
•数据卷挂载


## StatefulSet
StatefulSet与Deployment区别：有身份的！
•域名
•主机名
•存储（PVC）

```yaml
##控制器定义
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: web
spec:
  serviceName: "nginx"
  replicas: 2
  selector:
    matchLabels:
      app: nginx
##被控制对象
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.9.1
        ports:
        - containerPort: 80
          name: web
        volumeMounts:
        - name: www
          mountPath: /usr/share/nginx/html
  volumeClaimTemplates:
  - metadata:
      name: www
    spec:
      accessModes:
      - ReadWriteOnce
      resources:
        requests:
          storage: 1Gi
```
## DaemonSet
相比于 Deployment，DaemonSet 只管理 Pod 对象，然后通过 nodeAffinity 和 Toleration 这两个调度器的小功能，保证了每个节点上有且只有一个 Pod

## Job 与 CronJob
Deployment、StatefulSet，以及 DaemonSet 作用Nginx、Tomcat，以及 MySQL 等等。这些应用一旦运行起来，除非出错或者停止，它的容器进程会一直保持在 Running 状态
spec.parallelism，它定义的是一个 Job 在任意时间最多可以启动多少个 Pod 同时运行；
spec.completions，它定义的是 Job 至少要完成的 Pod 数目，即 Job 的最小完成数。
Job 对象在创建后，它的 Pod 模板，被自动加上了一个 controller-uid=< 一个随机字符串 > 这样的 Label
```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: pi
spec:
  parallelism: 2  
  completions: 4
  template:
    spec:
      containers:
      - name: pi
        image: resouer/ubuntu-bc
        command: ["sh", "-c", "echo 'scale=5000; 4*a(1)' | bc -l "]
      restartPolicy: Never
  backoffLimit: 4
```

## kubectl apply 声明式
kubectl apply -f nginx.yaml
kubectl replace 的执行过程，是使用新的 YAML 文件中的 API 对象，替换原有的 API 对象；而 kubectl apply，则是执行了一个对原有 API 对象的 PATCH 操作。

## API对象
创建 /apis/batch/v2alpha1 下的 CronJob 对象
```yaml
apiVersion: batch/v2alpha1
kind: CronJob
...
``` 
## 基于角色的权限控制：RBAC
Role：角色，它其实是一组规则，定义了一组对 Kubernetes API 对象的操作权限。
Subject：被作用者，既可以是“人”，也可以是“机器”，也可以使你在 Kubernetes 里定义的“用户”。
RoleBinding：定义了“被作用者”和“角色”的绑定关系。
```yaml
kind: Role
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  namespace: mynamespace
  name: example-role
rules:
- apiGroups: [""]
  resources: ["pods"]
  verbs: ["get", "watch", "list"]
```
```yaml
kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: example-rolebinding
  namespace: mynamespace
subjects:
- kind: User
  name: example-user
  apiGroup: rbac.authorization.k8s.io
roleRef:
  kind: Role
  name: example-role
  apiGroup: rbac.authorization.k8s.io
```


## Service Account
User Account：一般是独立于kubernetes之外的其他服务管理的用户账号。
Service Account：kubernetes管理的账号，用于为Pod中的服务进程在访问Kubernetes时提供身份标识。Service Account则属于某个具体的Namespace，DFAULT默认绑定
如果一个Pod在定义时没有指定spec.serviceAccountName属性，则会默认赋值为default

## Service
所谓 Service，其实就是 Kubernetes 为 Pod 分配的、固定的、基于 iptables（或者 IPVS）的访问入口。而这些访问入口代理的 Pod 信息，则来自于 Etcd，由 kube-proxy 通过控制循环来维护
Kubernetes 里面的 Service 和 DNS 机制，也都不具备强多租户能力。比如，在多租户情况下，每个租户应该拥有一套独立的 Service 规则（Service 只应该看到和代理同一个租户下的 Pod）。再比如 DNS，在多租户情况下，每个租户应该拥有自己的 kube-dns（kube-dns 只应该为同一个租户下的 Service 和 Pod 创建 DNS Entry）
## Ingress & LoadBalancer
这种全局的、为了代理不同后端 Service 而设置的负载均衡服务，就是 Kubernetes 里的 Ingress 服务。
LoadBalancer 类型的 Service，它会为你在 Cloud Provider（比如：Google Cloud 或者 OpenStack）里创建一个与该 Service 对应的负载均衡服务,成本较大每个service对应一个负载均衡

1)ClusterIP:集群内部使用
2)NodePort:对外暴露应用
3)LoadBalancer:对外暴露应用，适用公有云

## Iptables VS IPVS
流程包流程：客户端->NodePort/ClusterIP（iptables/Ipvs负载均衡规则）-> 分布在各节点Pod
1) 查看负载均衡规则：
•iptables模式
iptables-save |grep <SERVICE-NAME>
•ipvs模式
ipvsadm -L -n

2) 功能对比
Iptables：
•灵活，功能强大
•规则遍历匹配和更新，呈线性时延
IPVS：
•工作在内核态，有更好的性能
•调度算法丰富：rr，wrr，lc，wlc，ip hash...

## CoreDNS
CoreDNS：是一个DNS服务器，Kubernetes默认采用，以Pod部署在集群中，CoreDNS服务监视Kubernetes API，为每一个Service创建DNS记录用于域名解析。
ClusterIP A记录格式：<service-name>.<namespace-name>.svc.cluster.local
示例：my-svc.my-namespace.svc.cluster.local
kube-apiserver --请求service对应DNS-> CoreDNS Pod --> CoreDNS ClusterIp ---> Pod

```yaml
kind: Service
apiVersion: v1
metadata:
  name: example-service
spec:
  ports:
  - port: 8765
    targetPort: 9376
  selector:
    app: example
  type: LoadBalancer
```
```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: cafe-ingress
spec:
  tls:
  - hosts:
    - cafe.example.com
    secretName: cafe-secret
  rules:
  - host: cafe.example.com
    http:
      paths:
      - path: /tea
        backend:
          serviceName: tea-svc
          servicePort: 80
      - path: /coffee
        backend:
          serviceName: coffee-svc
          servicePort: 80
```

## resouces 资源限制
```yaml

```
## nodeSelector & nodeAffinity 亲和性
```yaml


```



## Operator 有状态应用
Operator 本身，其实就是一个 Deployment
Operator 的工作原理，实际上是利用了 Kubernetes 的自定义 API 资源（CRD），来描述我们想要部署的“有状态应用”；然后在自定义控制器里，根据自定义 API 对象的变化，来完成具体的部署和运维工作。
 StorageClass 的名称为 manual。 它将用于将 PersistentVolumeClaim 的请求绑定到此 PersistentVolume。
