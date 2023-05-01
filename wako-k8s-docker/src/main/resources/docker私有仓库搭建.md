## https://yeasy.gitbook.io/docker_practice/repository/registry

##docker安装 如果win10 docker desktop安装,windows PowerShell命令操作
```shell script
```
$ wget https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo -O /etc/yum.repos.d/docker-ce.repo
$ yum -y install docker-ce-18.06.1.ce-3.el7
$ systemctl enable docker && systemctl start docker
$ docker --version
```

```
cat > /etc/docker/daemon.json << EOF
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
        "10.199.210.66:5000"
    ]
}
EOF
```
```
##私有所在机器
```shell script
docker pull registry

docker run -d \
    -p 5000:5000 \
    -v /opt/data/registry:/var/lib/registry \
    registry

docker pull nginx 
docker image ls
docker tag nginx:latest 10.107.72.150:5000/nginx:latest
docker push 10.199.210.66:5000/nginx:latest
docker pull 10.199.210.66:5000/nginx:latest ##必须指定ip:port
curl 10.199.210.66:5000/v2/_catalog
```
##导入镜像
```shell script
#-o
docker save > kubernetes-server-linux-amd64-1.18.tar.gz 10.199.210.66:5000/k8s-server:v1.18
#-i
docker load < kubernetes-server-linux-amd64-1.18.tar.gz
```


## docker私有仓库缓存代理
```shell script
cat > /etc/docker/registry/config.yml << EOF
version: 0.1
log:
  fields:
    service: registry
storage:
  cache:
    blobdescriptor: inmemory
  filesystem:
    rootdirectory: /var/lib/registry
  delete:
    enabled: true
http:
  addr: :5000
  headers:
    X-Content-Type-Options: [nosniff]
health:
  storagedriver:
    enabled: true
    interval: 10s
    threshold: 3
proxy:
  remoteurl: https://registry-1.docker.io
EOF


docker run -d --name registry-proxy -v data_path:/opt/data/registry -v config_path:/etc/docker/registry/config.yml -p 5000:5000 registry

```
