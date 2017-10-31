

declare

  tb_name VARCHAR2(50);
  cursor table_cursor is
    select t.table_name
      from all_tables t
     where upper(t.table_name) in ('LOG_BIZ')
       and upper(t.owner) = upper((select username from user_users));

begin

  open table_cursor;
  LOOP
    exit when table_cursor%notfound;
    tb_name := null;
    FETCH table_cursor into tb_name;
    if tb_name is null then
      dbms_output.put_line('Basic tables have been cleared. ');
      exit;
    end if;
    dbms_output.put_line('drop table ' || tb_name);
    Execute Immediate ('drop table ' || tb_name);
  
  end LOOP;
  close table_cursor;

end;
/
 
/*1、LOG_BIZ 业务日志信息表：*/

-- Create table
create table LOG_BIZ (
  LOG_ID      VARCHAR2(32) default sys_guid() not null,
  LOG_LOGGER		VARCHAR2(50),
  LOG_THREAD	VARCHAR2(30),
  LOG_CLASS   	VARCHAR2(200),
  LOG_FUNCTION	VARCHAR2(50),
  LOG_LINE	  	VARCHAR2(3),
  LOG_LEVEL		VARCHAR2(30),
  LOG_MESSAGE	VARCHAR2(1000),
  LOG_EXCEPTION	CLOB,
  LOG_TIMESTAMP VARCHAR2(34) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')
);
-- Add comments to the table 
comment on table LOG_BIZ  is '业务日志信息表';
-- Add comments to the columns 
comment on column LOG_BIZ.LOG_ID  is '业务日志ID编号';
comment on column LOG_BIZ.LOG_LOGGER  is '业务日志记录器名称';
comment on column LOG_BIZ.LOG_THREAD  is '产生该业务日志的线程名';
comment on column LOG_BIZ.LOG_CLASS  is '产生该业务日志的Class名称';
comment on column LOG_BIZ.LOG_FUNCTION  is '产生该业务日志的函数名称';
comment on column LOG_BIZ.LOG_LINE  is '产生该业务日志的行号';
comment on column LOG_BIZ.LOG_LEVEL  is '业务日志级别';
comment on column LOG_BIZ.LOG_MESSAGE  is '业务日志内容';
comment on column LOG_BIZ.LOG_EXCEPTION  is '异常信息';
comment on column LOG_BIZ.LOG_TIMESTAMP  is '业务日志记录时间';
-- Create/Recreate primary, unique and foreign key constraints 
alter table LOG_BIZ add constraint PK_LOG_BIZ primary key (LOG_ID);

commit;
