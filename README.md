# ttdeye-stock


## 运维部署步骤
### 1，安装docker
*docker、docker-compose安装可自行百度，不做赘述 [百度](http://baidu.com)*
### 2，初始化环境
*宿主机分别创建目录（容器停止后保留数据）
*/data/logs/ttdeye-stock*
*/nginx/www*
*~/conf/nginx.conf*
*~/nginx/logs*
*/etc/nginx/conf.d*
*/data/redis/data*
*/data/mysql/conf*
*/data/mysql/logs*
*/data/mysql/data*
### 3,上传docker-compose.yml

### 4，项目构建
*1，分别执行maven clean install、maven docker:bulid、maven docker:push（此时已将项目镜像打包上传到docker hub）
 
### 5，使用docker-compose 启动依赖容器

### 6，初始化数据库

### 7，使用docker-compose 启动ttdeye-stock

### 8，配置nginx反向代理等
        



