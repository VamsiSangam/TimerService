create table timer_service_users
(
    email varchar(100) constraint TIMER_SERVICE_USERS_EMAIL_PK primary key,
    password varchar(100) constraint TIMER_SERVICE_USERS_PASSWORD_NN not null,
    constraint TIMER_SERVICE_USERS_PASSWORD_MIN_LEN check(len(password) >= 6)
);

create table timer_service_timers
(
    id integer identity(1, 1) constraint TIMER_SERVICE_TIMERS_ID_PK primary key,
    email varchar(100) constraint TIMER_SERVICE_TIMERS_EMAIL_FK foreign key references timer_service_users(email),
    expiry datetime constraint TIMER_SERVICE_TIMERS_EXPIRY_NN not null,
    constraint TIMER_SERVICE_TIMERS_EXPIRY_VALIDITY check(expiry > sysdatetime())
);



root
   |___ index.html
   |___ register.html
   |___ Users
            |___ index.jsp
            |___ addTimer.html

source packages
              |___ servlets
                          |___ Validate.java
                          |___ CheckTimer.java
                          |___ AddUser.java
                          |___ AddTimer.java
                          |___ Logout.java
              |___ filter
                        |___ UserFilter.java