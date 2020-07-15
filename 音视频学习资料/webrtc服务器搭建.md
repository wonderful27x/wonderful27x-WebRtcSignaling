
> Webrtc服务器搭建后台项目地址
>
> JAVA项目 : https://github.com/androidtencent/WebRtcJavaWeb
		   ：https://github.com/ddssingsong/webrtc_server_java
>
> NodeJs项目 : https://github.com/androidtencent/WebrtcNodeJS
             ：https://github.com/ddssingsong/webrtc_server_node


**本搭建是基于centos  7.6 64位系统，系统恢复原始状态，重新装系统，确保人人都能搭建成功（使用NodeJs项目）**

如果系统安装了基础软件  如git  gcc++  可以跳该步骤

```
yum update
yum install git
yum install  make
yum install gcc-c++
```

#### 1.1  搭建Node环境（js需要运行在Node环境中，如同java运行在jre一样）

下载官网最新nodejs：https://nodejs.org/en/download

```
mkdir webrtc
cd webrtc
wget https://nodejs.org/dist/v10.16.0/node-v10.16.0-linux-x64.tar.xz
```

```
# 解压
tar -xvf node-v10.16.0-linux-x64.tar.xz
# 改名
mv node-v10.16.0-linux-x64 nodejs
# 进入目录
cd nodejs/

# 确认一下nodejs下bin目录是否有node 和npm文件，如果有就可以执行软连接
sudo ln -s /root/webrtc/nodejs/bin/npm /usr/local/bin/
sudo ln -s /root/webrtc/nodejs/bin/node /usr/local/bin/

# 看清楚，这个路径是你自己创建的路径，我的路径是/home/dds/webrtc/nodejs

#查看是否安装
node -v 
npm -v 

# 注意，ubuntu 有的是需要sudo,如果不想sudo,可以
sudo ln -s /root/webrtc/nodejs/bin/node /usr/bin/
```

#### 1.2安装Webrtc服务端（这个服务就是socket信令服务）

安装webrtc服务器和浏览器端
```
git clone https://github.com/androidtencent/WebrtcNodeJS
cd WebrtcNodeJS
npm install

修改 WebrtcNodeJS/public/dist/js/SkyRTC-client.js：(浏览器端的访问配置) 

   const iceServer = {
        "iceServers": [
          {
            "url": "stun:stun.l.google.com:19302"
          },
          {
            "url": "stun:114.55.252.175:3478"
          },
          {
             "url": "turn:114.55.252.175:3479",
             "username":"wonderful",
             "credential":"wonderful@123"
          }
        ]
    };
```

#### 1.3 安装turn服务器的环境准备（需要用到数据库，加密证书等）

```
cd ..
yum install openssl openssl-libs libevent2 libevent-devel
yum install openssl-devel
yum install sqlite
yum install sqlite-devel
yum install postgresql-devel
yum install postgresql-server
yum install mysql-devel
yum install mysql-server
yum install hiredis
yum install hiredis-devel
```

#### 1.4开始安装turn服务器（这就是ICE服务，用于内网穿透及转发，虽然叫coturn，但他整合了stun和turn）

```
git clone https://github.com/coturn/coturn 
cd coturn 
./configure 
make 
sudo make install
```

查看是否安装成功

```
which turnserver
```

生成用户名和密码及密钥

```
turnadmin -k -u wonderful -r wonderful.webrtc -p 123456
```

其中的参数含义 
-a: 表示使用lt-cred-mech方式连接 （暂时不用）
-u：用户名 
-p：密码 
-r ：realm 域。自己试了，似乎随便写，没啥影响,猜测有自己的域名才使用。

自动生成密钥如下：
```
0xfb76c57e823de97df580e573437ef54a（记住这串密钥）
0: log file opened: /var/log/turn_1791_2019-07-31.log
0: SQLite connection was closed.
```

使用openssl生成证书(注意两个.pem文件的保存位置)：
```
sudo openssl req -x509 -newkey rsa:2048 -keyout /etc/turn_server_pkey.pem -out /etc/turn_server_cert.pem -days 99999 -nodes
```

接下来配置turnserver的配置文件，配置文件存放在/usr/local/etc/turnserver.conf文件下

这个文件本身是不存在的，需要我们自己创建，下面是具体内容及注释（更详细的注释到安装目录的examples下查看）：

==============================================start========================================
#监听的设备
listening-device=eth0
#turnserver监听UDP/TCP端口,默认为3478;
listening-port=3478/3479                 
#turnserver监听TLS/DTLS端口,默认为5349
tls-listening-port=5349             

#中继服务器的监听IP地址,可以配置多个,如果不配置则所有IPv4和IPv6系统IP都将用于侦听，
#这时会同时出现3478和3479两个端口。当然也可以用下面这种方式结合listening-port=3478/3479
#设置成两个监听端口，可分别用于stun和turn，不过顺序也可颠倒。但是如果只配置了一个端口，
#那么stun和turn就必须都使用这个端口，他们使用了相同的端口也是没问题的。
listening-ip=172.16.98.79/172.16.98.79                        
#中继服务器的IP地址,可以和监听地址一样 
relay-ip=172.16.98.79                   
#外部IP,当中继服务器在NAT网络内部时指定,此处可以不添加;
external-ip=114.55.252.175/172.16.98.79    

#Lower and upper bounds of the UDP relay endpoints
#(default values are 49152 and 65535)
min-port=49152     
max-port=65535

#run TURN server in 'normal' 'moderate' verbose mode.        
verbose
#use fingerprints in the TURN messages
fingerprint
                
#webrtc需要使用此选项，使用证书认证（证书认证和静态身份认证不能同时使用）
lt-cred-mech  
                                   
#使用静态身份认证，后面指定的是用户名
# static-auth-secret=USERNAME     
#用户名：密钥（turnadmin ... 生成的xxx密钥）
user=wonderful:0xd8c3163bc7d986be408bfca38efaea00        
#用户名:密码（turnadmin -p参数）,官方说明中这种写法和上面是or关系，上面的更安全些
#user=wonderful:wonderful@123
        
#指定加密算法       
#sha256                                      

# SQLite database file name.
# Default file name is /var/db/turndb or /usr/local/var/db/turndb or
# /var/lib/turn/turndb
#userdb=/var/lib/turn/turndb

#域（turnadmin -r参数）-r 中可以随意写，一致就行                           
realm=wonderful.ice.coturn 
                                     
#nonce生命周期，如果像这里不指定具体的值则使用默认值600（秒）
stale-nonce 
                                          
#指定认证文件
#生成方法：sudo openssl req -x509 -newkey rsa:2048 -keyout /etc/turn_server_pkey.pem -out /etc/turn_server_cert.pem -days 99999 -nodes
cert=/opt/TURN/coturn/turnserver_cert.pem         
#指定认证文件  
pkey=/opt/TURN/coturn/turnserver_pkey.pem

#安全设置，禁止环回网卡
no-loopback-peers       
#安全设置，禁止知名广播地址                       
no-multicast-peers   
                          
#支持mobility ICE协议
mobility         
#只使用stun服务
#stun-only
#不使用stun服务
#no-stun
   
#关闭cli                            
no-cli
   
#控制台不输出日志
#no-stdout-log
#日志信息
log-file=/opt/TURN/coturn/turnserver.log
#进程信息
pidfile=/opt/TURN/coturn/turnserver.pid
=======================================================end====================================================


#### 1.5  安装nginx服务器
#### nginx是一个http和反向代理服务器，作为http服务器他的功能和Apache等同，但是在高并发方面更优秀，
#### 作为反向代理服务器可以为其他服务器做代理，均衡各服务器。使用阿里云注意配置相应端口安全组权限
```
wget http://nginx.org/download/nginx-1.17.8.tar.gz
tar -zxvf nginx-1.17.8.tar.gz
cd nginx-1.17.8

#编写编译脚本 build.sh
#!/bin/bash
./configure \                                  
--prefix=./bin \                              #安装目录
--with-http_stub_status_module \              #开启状态管理
--with-http_ssl_module \                      #开启https
--add-module=/opt/MODULES/nginx-rtmp-module   #开启rtmp推流功能，nginx-rtmp-module需要提前下载，这个选项和webrtc无关，可以不加

./build.sh
make install
```

#### 1.6 更改nginx 配置文件   （额外强调  其中包含https证书，下面会告诉生成方式）

vim /opt/NGINX/nginx/bin/conf/nginx.conf  (注意这个目录就是上面”--prefix=./bin“指定的安装目录)

删除配置文件内容,更改为以下内容

![3](img/3.png)

```
user root;
worker_processes auto;

pid /opt/NGINX/nginx/nginx.pid;
error_log /opt/NGINX/nginx/bin/logs/error.log debug;

events {
	worker_connections 1024;
	multi_accept on;
}

http {
	sendfile on;
	tcp_nopush on;
	tcp_nodelay on;
	keepalive_timeout 300;
	types_hash_max_size 2048;
	default_type application/octet-stream;

	ssl_protocols TLSv1 TLSv1.1 TLSv1.2; # Dropping SSLv3, ref: POODLE
	ssl_prefer_server_ciphers on;
	
	access_log /opt/NGINX/nginx/bin/logs/access.log;

	gzip on;

    upstream web {
		server localhost:3000;      
    }
	
	upstream websocket {
		server localhost:3000;   
    }

	server { 
		listen 443 ssl;
		server_name  localhost;

		ssl_certificate      /opt/NGINX/nginx/keys/cert.crt; #配置证书
		ssl_certificate_key  /opt/NGINX/nginx/keys/cert.pem; #配置密钥
		ssl_session_cache    shared:SSL:1m;
		ssl_session_timeout  50m;
		ssl_protocols TLSv1 TLSv1.1 TLSv1.2 SSLv2 SSLv3;
		ssl_ciphers  HIGH:!aNULL:!MD5;
		ssl_prefer_server_ciphers  on;
		
		#android端websocket
		location /wss {
			proxy_pass http://websocket/; #代理到上面的地址去
			proxy_read_timeout 300s;
			proxy_set_header Host $host;
			proxy_set_header X-Real_IP $remote_addr;
			proxy_set_header X-Forwarded-for $remote_addr;
			proxy_set_header Upgrade $http_upgrade;
			proxy_set_header Connection 'Upgrade';	
 		}
		
		#浏览器端访问https://11.55.234.123/#123456
		location / {
			proxy_pass         http://web/;
			proxy_set_header   Host             $host;
			proxy_set_header   X-Real-IP        $remote_addr;
			proxy_set_header   X-Forwarded-For  $proxy_add_x_forwarded_for;
 		}
	}
	
	server {
        #注意端口占用
        listen      8027;
        location /stat {
            rtmp_stat all;
            rtmp_stat_stylesheet stat.xsl;
        }
        location /stat.xsl {
            #注意目录
            root /opt/MODULES/nginx-rtmp-module/;
        }
        location /control {
            rtmp_control all;
        }
        location /rtmp-publisher {
            #注意目录
            root /opt/MODULES/nginx-rtmp-module/test;
        }
        location /test {
            #注意目录
            root /opt/MODULES/nginx-rtmp-module/test/www;
        }
    }
}
rtmp {
    server {
        #注意端口占用，1935为默认端口
        listen 1935;
        application wonderful {
            live on;
            #丢弃闲置5s的连接
            drop_idle_publisher 5s;
        }
    }
}




```

#### 1.7 生成nginx中的https证书

	https是http security的缩写，即安全的http服务，一般是http + SSL/TLS,从结构上来说SSL/TLS是架设在运输层和应用层中间的一层安全协议，
 一般在C/S架构下会首先使用非对称加密算法来协商出用于数据加密的对称密钥，一般是客户端产生这个对称加密密钥，然后使用服务端的公钥进行加密传给服务器，
 服务端用私钥进行解密。然而如果攻击者伪装成服务器将自己的公钥给客户端进行加密再用自己的私钥进行解密，那么他就能获得双方的数据加密密钥，这仍然是危险的，
 所以客户端需要确认公钥是服务器的而不是攻击者的，这就需要一个权威机构-认证中心CA来将某个公钥与其对应的实体绑定，这就是数字证书，
 证书包含了公钥以及公钥的持有者的信息，同时CA认证中心用自己的私钥对证书进行数字签名防止证书的伪造和篡改。我们应当注意到CA认证是为解决公钥的安全分配而产生的。
	在https通信前，服务器会将自己的数字证书发送给客户端，客户端使用CA认证中心的公钥来认证证书的有效性，然后就能取出数字证书里面的公钥（服务器的公钥）
 进行后续的操作了。客户端如浏览器一般会设置一些根证书，我的理解就是根证书包含了受信任的认证中心的公钥，用于认证数字证书的有效性。所以我们可以看到
 其中包含了两个重要的证书，CA的根证书和CA签发的服务器证书。上面说的是单项认证的情况，有的还会后CA签发的客户端证书，即客户端需要认证服务器，服务器
 也需要认证客户端。
	由于找权威的认证中心签发证书费用昂贵，我们可以使用openssl自己制作证书，然后自己充当CA认证机构给证书签名。这书的格式一般使用X.509协议标准。
	https://blog.csdn.net/gengxiaoming7/article/details/78505107
	
	一、自己充当CA认证机构，制作认证中心的CA根证书
	1.生成CA根证书私钥
	#生成2048位非对称加密私钥wonderful_ca_private.key，
	#-des3表示对生成的私钥使用CBC模式的DES加密算法进行对称加密
	openssl genrsa -des3 -out wonderful_ca_private.key 2048
	2.为了方便将加密的私钥转成非加密的私钥
	openssl rsa -in wonderful_ca_private.key -out wonderful_ca_private.key
	3.生成CA根证书签名请求文件
	openssl req -new -key wonderful_ca_private.key -out wonderful_ca.csr
	4.使用CA根证书私钥签署CA根证书签名请求文件生成自签名证书，将这个自签名证书作为认证中心的CA根证书
	#生成x509协议证书，有效期3650天-10年
	openssl x509 -req -in wonderful_ca.csr -out wonderful_ca.crt -signkey wonderful_ca_private.key -days 3650
	#注意，其中3、4步骤可以合并成一步
	openssl req -new -x509 -key wonderful_ca_private.key -out wonderful_ca.crt -days 3650
	
	二、生成服务器证书签名请求文件
	1.生成服务器端私钥
	openssl genrsa -des3 -out service_private.key 2048
	2.转成非加密私钥
	openssl rsa -in service_private.key -out service_private.key
	3.生成证书签名请求文件
	openssl req -new -key service_private.key -out service_request.csr
	
	三、将服务器证书签名请求文件给CA认证中心进行签名生成最终的服务器数字证书
	#使用认证中心CA的根证书对请求文件进行签名生成服务器证书，有效期3650天-10年
	openssl x509 -req -in service_request.csr -CA wonderful_ca.crt -CAkey wonderful_ca_private.key -CAcreateserial -out service.crt -days 3650
	
	四、合并证书（不太理解）
	cat service_private.key service.crt > service.pem
	

 **x509证书一般会用到三类文，key，csr，crt**

 **Key 是私用密钥openssl格，通常是rsa算法。**

 **Csr 是证书请求文件，用于申请证书。在制作csr文件的时，必须使用自己的私钥来签署申，还可以设定一个密钥。**

 **crt是CA认证后的证书文，（windows下面的，其实是crt），签署人用自己的key给你签署的凭证。** 

 
 **1. key的生成** 
 
 生成rsa私钥，des3算法，openssl格式，2048位强度。cert.key是密钥文件名，为了生成这样的密钥，需要一个至少四位的密码。

 ```
 openssl genrsa -des3 -out cert.key 2048
 ```

 可以通过以下方法生成没有密码的key:
 ```
 openssl rsa -in cert.key -out cert.key
 ```

 **2. 生成CA的crt**

 ```
 openssl req -new -x509 -key cert.key -out cert.crt -days 3650
 ```

 生成的cert.crt文件是用来签署下面的cert.csr文件。 
  
 **3. csr的生成方法**
 
 需要依次输入国家，地区，组织，email。最重要的是有一个common name，可以写你的名字或者域名。
 如果为了https申请，这个必须和域名吻合，否则会引发浏览器警报。生成的csr文件交给CA签名后形成服务端自己的证书。 
 
 ```
 openssl req -new -key cert.key -out cert.csr
 ```

 **4. crt生成方法**

 CSR文件必须有CA的签名才可形成证书，可将此文件发送到verisign等地方由它验证，要交一大笔钱，何不自己做CA呢。

 ```
 openssl x509 -req -days 3650 -in cert.csr -CA cert.crt -CAkey cert.key -CAcreateserial -out cert.crt
 ```

 输入key的密钥后，完成证书生成。-CA选项指明用于被签名的csr证书，-CAkey选项指明用于签名的密钥，-CAserial指明序列号文件，而-CAcreateserial指明文件不存在时自动生成。

 最后生成了私用密钥：cert.key和自己认证的SSL证书：cert.crt

 证书合并：

 ```
 cat cert.key cert.crt > cert.pem
 ```

#### 1.8启动nginx（代理服务）、turnserver（ICE服务）、webrtc(信令服务)服务

启用后台服务，下面是一个启动脚本

启动脚本：
#!/bin/bash
#node的路径
NODE=`which node`
#turnserver的路径
TURN=`which turnserver`
#开一个进程后台运行webrtc服务,这是一个信令服务器
nohup $NODE /opt/WEBRTC/WebrtcNodeJS/server.js & > /opt/WEBRTC/WebrtcNodeJS/webrtc.log
#开一个进程后台运行turnserver服务，他集成了turn和stun，是一个ice服务器：-o 后台运行，-c 指定配置文件
nohup $TURN -o -c /usr/local/etc/turnserver.conf
#开启nginx服务，这是一个代理服务器
cd /opt/NGINX/nginx
./bin/sbin/nginx

停止脚本：
#!/bin/bash
pkill node
pkill turnserver
pkill nginx


