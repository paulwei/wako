##centos7 防火墙端口打开，添加端口，然后再关闭
firewall-cmd --zone=public --add-port=80/tcp --permanent    （--permanent永久生效，没有此参数重启后失效）
iptables -A INPUT -i eth1 -s 0.0.0.0/0 -p tcp -j ACCEPT
iptables -A OUTPUT -o eth1 -d 0.0.0.0/0 -p tcp -j ACCEPT

iptables -L INPUT -n -v
iptables -L OUTPUT -n -v --line-numbers
service iptables stop
service iptables start
service iptables restart


iptables -A OUTPUT -o eth1 -d 10.107.1.0/24 -p tcp -j ACCEPT
