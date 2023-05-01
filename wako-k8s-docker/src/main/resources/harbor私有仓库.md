##docker-compose��װ
#1.docker-ce�İ�װ��ο����ͣ�https://blog.51cto.com/wangxiaoke/2174103
#2.harbor�ֿ⣺https://github.com/goharbor/harbor
#3.docker-compose�ֿ⣺https://github.com/docker/compose/ 
#4.docker-ce�汾��Docker version 18.06.1-ce, build e68fc7a
#5.docker-compose�汾��docker-compose version 1.23.0, build c8524dc1
#6.harbor�汾��1.7.4
#https://github.com/docker/compose/releases/download/1.23.0/docker-compose-linux-x86_64
cp  docker-compose-Linux-x86_64 /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
docker-compose start 

# ���أ� https://github.com/goharbor/harbor/releases/
wget https://github.com/goharbor/harbor/releases/download/v2.3.2/harbor-offline-installer-v2.3.2.tgz

# ��ѹ
tar xvf harbor-offline-installer-v2.3.2.tgz

# ����harbor
cd harbor

# ���������ļ�`harbor.yml`
cp harbor.yml.tmpl harbor.yml
# �޸�����(�ɲο����������demo)
vim harbor.yml
## ERROR:root:Error: The protocol is https but attribute ssl_cert is not set ע��https
# ��װ
./install.sh
docker-compose ps

##�޸�harbor.yaml������,����cd /opt/harbor
docker-compose down
./prepare
docker-compose up -d

##������� docker-hub-proxy �½���Ŀdocker-proxy ѡ�� docker-proxy
##�����ƾ���https://yrg1jgp2.mirror.aliyuncs.com /etc/docker/daemon.json
1) vim /etc/docker/daemon.json 
"insecure-registries": ["192.168.1.100"]
"registry-mirrors": ["https://yrg1jgp2.mirror.aliyuncs.com"]
systemctl daemon-reload 
systemctl restart docker
2) /usr/lib/systemd/system/docker.service 
ExecStart=/usr/bin/dockerd --insecure-registry=192.168.1.100
ExecStartPost=/sbin/iptables -I FORWARD -s 0.0.0.0/0 -j ACCEPT
systemctl daemon-reload 
systemctl restart docker
systemctl restart docker


##192.168.1.10 ��¼  Error response from daemon: Get http://192.168.1.100/v2/: Get http://192.168.1.100:5000/service/token?account=admin&client_id=docker&offline_token=true&service=harbor-registry: dial tcp 192.168.1.100:5000: connect: connection refused
docker login 192.168.1.100
docker pull 192.168.1.100/docker-proxy/library/nginx:latest

##windows10 harbor��װ windows PowerShell�������
dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
Enable-WindowsOptionalFeature -Online -FeatureName VirtualMachinePlatform
wsl --set-default-version 2


