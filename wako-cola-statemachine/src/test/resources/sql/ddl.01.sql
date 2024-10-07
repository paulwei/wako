create table if not exists wcs_asrs_task
(
  id bigint(20) unsigned not null auto_increment comment '主键',
  task_no varchar(200) not null default '' comment '任务编号',
  task_status varchar(200) not null default '' comment '任务状态',
  task_event varchar(200) not null default '' comment '任务事件',
  create_time timestamp not null default current_timestamp comment '创建时间',
  update_time timestamp not null default current_timestamp comment '更新时间',
  create_by varchar(200) not null default '' comment '创建者',
  update_by varchar(200) not null default '' comment '更新者',
  record_version bigint(20) not null default 0 comment '版本号',
  primary key(id),
  key idx_wcs_asrs_task_create_time(create_time),
  key idx_wcs_asrs_task_task_status(task_status,create_time),
  key idx_wcs_asrs_task_task_event(task_event,create_time)
) ENGINE=InnoDB DEFAULT charset=UTF8 comment '蜂巢任务';
