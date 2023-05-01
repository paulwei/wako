#master服务器
$ yum -y install nfs-utils rpcbind
# 共享目录
$ mkdir -p /data/k8s && chmod 755 /data/k8s
#授予所有连接访问权限
$ echo '/data/k8s  *(insecure,rw,sync,no_root_squash)'>>/etc/exports
#启动服务与设置开机启动
$ systemctl enable rpcbind && systemctl start rpcbind
$ systemctl enable nfs && systemctl start nfs

#分别在各slave节点上安装nfs客户端
$ yum -y install nfs-utils rpcbind
$ mkdir /nfsdata
#验证挂载
$ mount -t nfs 10.199.210.66:/data/k8s /nfsdata

 
