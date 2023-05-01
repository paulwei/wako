##创建CA根证书及其私钥
根证书及其私钥，也即CA证书和CA证书的私钥，是集群所有节点共享的，只需要创建一个 CA 证书即其私钥，后续创建的所有证书都由它签名。
CA根证书创建后一般命名是ca.pem
CA更证书私钥创建后一般命名是ca-key.pem
备注：CA根证书及其私钥，只需要创建一次即可。后续其他证书都由它签名，CA根证书及其私钥一旦改变，其它证书也就无效了

##(一)创建CA根证书签名请求文件ca-csr.json
注意ca-csr.json中csr含义是：Certificate Signing Request，证书签名请求，因此看到 xx-csr.json，你就要知道该文件是证书签名请求文件，有了它，cfssl就可以生成证书和证书私钥
CN：Common Name，所有csr文件都必须有字段，对于 SSL 证书，一般为网站域名；而对于代码签名证书则为申请单位名称；而对于客户端证书则为证书申请者的姓名。
hosts: 网络请求url中的合法主机名或域名集合
```json
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
```
##(二)根据CA根证书签名请求文件生成CA根证书及其私钥  
###CA根证书创建后一般命名是ca.pem,CA更证书私钥创建后一般命名是ca-key.pem
cfssl gencert -initca ca-csr.json | cfssljson -bare ca

##(三)根据CA根证书及其私钥签名生成其它证书及其私钥
1) CA根证书配置文件 ca-config.json  
```json
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
```
expiry: 87600h表示10年有效期
profiles: 指定证书使用场景，下面的kubernetes是一个场景名称，后续签名生成证书及其私钥时需要指定该场景(profile)名称
signing：表示该证书可用于签名其它证书，生成的 ca.pem 证书中 CA=TRUE
server auth：表示 client 可以用该该证书对 server 提供的证书进行验证
client auth：表示 server 可以用该该证书对 client 提供的证书进行验证
2) 创建目标证书签名请求文件 server-csr.json
```json
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
```
3) 根据CA根证书及其私钥签名生成目标证书和私钥
cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=kubernetes server-csr.json | cfssljson -bare server

4) 校验证书 cfssl-certinfo -cert server.pem

5）分发证书 签名生成的证书文件server.pem及其私钥文件server-key.pem 拷贝到集群所有需要的地方，即分发证书


CA认证的原理
　　通过下面介绍信的描述介绍CA的原理。
　　◇ 普通的介绍信
　　想必大伙儿都听说过介绍信的例子吧？假设 A 公司的张三先生要到 B 公司去拜访，但是 B 公司的所有人都不认识他，他咋办捏？常用的办法是带公司开的一张介绍信，在信中说：兹有张三先生前往贵公司办理业务，请给予接洽......云云。然后在信上敲上A公司的公章。
　　张三先生到了 B 公司后，把介绍信递给 B 公司的前台李四小姐。李小姐一看介绍信上有 A 公司的公章，而且 A 公司是经常和 B 公司有业务往来的，这位李小姐就相信张先生不是歹人了。
   这里，A公司就是CA证书
　　
   ◇ 引入中介机构的介绍信
　　好，回到刚才的话题。如果和 B 公司有业务往来的公司很多，每个公司的公章都不同，那前台就要懂得分辨各种公章，非常滴麻烦。所以，有某个中介公司 C，发现了这个商机。C公司专门开设了一项“代理公章”的业务。
　　今后，A 公司的业务员去 B 公司，需要带2个介绍信：

　　介绍信1
　　含有 C 公司的公章及 A 公司的公章。并且特地注明：C 公司信任 A 公司。
　　介绍信2
　　仅含有 A 公司的公章，然后写上：兹有张三先生前往贵公司办理业务，请给予接洽......云云。
　　某些不开窍的同学会问了，这样不是增加麻烦了吗？有啥好处捏？
　　主要的好处在于，对于接待公司的前台，就不需要记住各个公司的公章分别是啥样子的；他/她只要记住中介公司 C 的公章即可。当他/她拿到两份介绍信之后，先对介绍信1的 C 公章，验明正身；确认无误之后，再比对介绍信1和介绍信2的两个 A 公章是否一致。如果是一样的，那就可以证明介绍信2是可以信任的了。
