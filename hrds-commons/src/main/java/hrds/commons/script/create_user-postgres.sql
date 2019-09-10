service postgresql-9.6 restart
service postgresql-9.6 initdb


vi /var/lib/pgsql/9.6/data/postgresql.conf
	listen_addresses="*"
vi /var/lib/pgsql/9.6/data/pg_hba.conf
	host    all             all             172.168.0.0/24          trust


create user hrds with superuser password 'hrds';
create database hrds WITH ENCODING='UTF8' OWNER=hrds;
create database dqc WITH ENCODING='UTF8' OWNER=hrds;

--如果没有elk或者gp使用postgresql做增量时 需要创建相关服务和指定表空间 命令如下:

CREATE EXTENSION file_fdw;

create server pg_file_server foreign data wrapper file_fdw;

--目录 /var/lib/pgsql/9.6/ 是postgresql安装目录 使用root用户 在下面创建新文件夹data2作为数据存储目录,并修改权限
-- cd /var/lib/pgsql/9.6/
-- mkdir data2
-- chown postgres:postgres data2
create tablespace hrds_tablespace location '/var/lib/pgsql/9.6/data2';


--elk语句

创建用户并指定密码（密码）
CREATE USER hrds PASSWORD 'hrds@hyren1';

创建到hdfs的表空间

假如用户要创建一个名为hrds_tablespace的HDFS表空间。假定/home/omm/bigdata/hdfs_ts是omm用户拥有读写权限的空目录。cfgpath为HDFS集群配置文件的路径，/hrds/elk/data为HDFS上数据存储的目录。
请确定所有目录是否正确

CREATE TABLESPACE hrds_tablespace LOCATION '/home/omm/bigdata/hdfs_ts' WITH(filesystem=hdfs, cfgpath='/opt/huawei/Bigdata/mppdb/conf' , storepath='/hrds/elk/data');

给指定表空间赋权限

GRANT CREATE ON TABLESPACE hrds_tablespace TO hrds;

创建数据库指定编码utf8
CREATE DATABASE hrds ENCODING 'UTF8' template = template0 OWNER hrds;
创建数据库指定编码gbk
CREATE DATABASE hrds ENCODING 'GBK' template = template0 OWNER hrds;


--GreenPlum语句

创建用户并指定密码

查看已有role
select rolname,oid from pg_roles;

创建role
create role hrds password 'hrds@hyren1';

创建表空间

查看已有表空间和文件空间
select a.spcname,b.fsname from pg_tablespace a,pg_filespace b where spcfsoid=b.oid;

查看已有文件空间(要创建新的文件空间请查看greenplum安装文档)
select * from pg_filespace;

创建表空间（首先创建文件空间）
CREATE TABLESPACE hrds_tablespace FILESPACE hrdsfilespace;

给指定表空间赋权限到指定角色

GRANT CREATE ON TABLESPACE hrds_tablespace TO hrds;

创建数据库指定编码utf8

CREATE DATABASE hrds ENCODING 'UTF8'  OWNER hrds;

如果连接不上需要修改配置文件，还需要修改pg_hba.conf文件，来赋予用户的远程登录权限。
