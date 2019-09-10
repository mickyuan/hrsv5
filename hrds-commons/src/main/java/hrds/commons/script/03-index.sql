


ALTER TABLE dq_dl_log DROP COLUMN attc;
ALTER TABLE dq_dl_log ADD COLUMN attc bytea;

ALTER TABLE source_file_attribute ALTER COLUMN "meta_info" TYPE text;
ALTER TABLE source_file_detailed ALTER COLUMN "meta_info" TYPE text;
ALTER TABLE sdm_consume_des ALTER COLUMN "sdm_bus_pro_cla" TYPE text;
ALTER TABLE sdm_consume_des ALTER COLUMN "des_class" TYPE text;
ALTER TABLE sdm_receive_conf ALTER COLUMN "sdm_bus_pro_cla" TYPE text;
ALTER TABLE failure_table_info ALTER COLUMN "table_meta_info" TYPE text;
ALTER TABLE failure_column_info ALTER COLUMN "column_meta_info" TYPE text;
ALTER TABLE auto_tp_info ALTER COLUMN "template_sql" TYPE text;
ALTER TABLE auto_fetch_sum ALTER COLUMN "fetch_sql" TYPE text;
ALTER TABLE auto_comp_sum ALTER COLUMN "exe_sql" TYPE text;
ALTER TABLE auto_comp_sum ALTER COLUMN "component_buffer" TYPE text;
alter table interface_use_log alter column request_info  TYPE text;
alter table source_operation_info alter column execute_sql TYPE text;
alter table sdm_sp_analysis alter column analysis_sql TYPE text;

alter table etl_job_def alter column pro_para TYPE text;
alter table etl_job alter column pro_para TYPE text;
alter table etl_job_disp_his alter column pro_para TYPE text;



ALTER TABLE etl_job_disp_his DROP constraint etl_job_disp_his_pk;


create unique index u_index_data_source01 on data_source(datasource_number);-- 数据源编号不能重复
create unique index u_index_database_set01 on database_set(database_number);-- 数据库设置ID不能重复


-- 源文件属性
create index index_source_file_attribute01 on source_file_attribute(collect_set_id,source_path);
create index index_source_file_attribute02 on source_file_attribute(source_path);
create index index_source_file_attribute03 on source_file_attribute(file_md5);
create index index_source_file_attribute04 on source_file_attribute(agent_id);
create index index_source_file_attribute05 on source_file_attribute(source_id);
create index index_source_file_attribute06 on source_file_attribute(collect_set_id);
create index index_source_file_attribute07 on source_file_attribute(hbase_name);
CREATE INDEX index_source_file_attribute08 ON source_file_attribute(lower(hbase_name));
create index index_source_file_attribute10 on source_file_attribute(file_avro_path);
create index index_source_file_attribute09 on source_file_attribute(collect_type);


-- 采集情况信息表
create index index_collect_case01 on collect_case(agent_id);
create index index_collect_case02 on collect_case(collect_set_id);
create index index_collect_case03 on collect_case(source_id);
create index index_collect_case04 on collect_case(etl_date);

-- 文件采集统计表

CREATE TABLE file_hbase (
	rowkey CHARACTER VARYING(256),
	hyren_s_date CHARACTER VARYING(8),
	hyren_e_date CHARACTER VARYING(8),
	file_md5 CHARACTER VARYING(32),
	file_avro_path CHARACTER VARYING(512),
	file_avro_block NUMERIC(15,0)
);