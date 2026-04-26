Create table employees (  
id serial primary key, 
name VARCHAR(100), 
mail varchar(100), 
hashed_password VARCHAR(200), 
phone_number varchar(30), 
manager_id int, 
session_version int, 
Constraint manager_key foreign key (manager_id) references employees(id) on delete set null 
); 


create table leaves (
    id serial primary key,
    emp_id int,
    approver_id int,
    applied_on DATE,
    reason varchar(1000),
    approval_status varchar(50),
    dates JSONB,
    comments varchar(500),
    foreign key (emp_id) references employees(id)
)

create table employee_leaves(
    emp_id int,
    allowed_paid_leaves int,
    rem_paid_leaves int,
)