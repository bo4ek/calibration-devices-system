
# Calibration devices system 

Centralized system for calibration of measurement devices. Enterprise open-source project intended to optimize process of verification calibration devices. The main goal of the project is to create centralized system of measurement devices for calibration and verification processes in Ukraine.

### Installation dependencies

The following dependencies are necessary:

 - Java 8
 - Bower
 - Maven 3
 - MySQL

### Installing frontend dependencies

Run the following command on the root folder of the repository:

    bower install

### Building and starting the server

Before running the app, create a MySQL database called `measurement_devices` with charset encoding UTF-8 (more specifically with collation `utf8_unicode_ci`). 
Then run the following command on the root folder of the repository:

    mvn clean install tomcat7:run-war

 Once hibernate creates tables  use `database_script.sql` to fill data.

After the server starts, the application is accessible at the following URL:

    http://localhost:8080/
    
To get an admin page login with the following credentials:

    username: admin
    password: password

To get an super admin page login with the following credentials:

        username: super
        password: super
	
To get an provider page login with the following credentials:
	
	username: provider-lv
	password: pass

To get an calibrator page login with the following credentials:
	
	username: calibrator-lv
	password: pass	
	
To get an verificator page login with the following credentials:
	
	username: verificator-lv
	password: pass
	
Check `database_script.sql` for additional information about other users such as provider, calibrator etc.

##Optional:
 Adding integration Tests.
 - Create DataBase with name: measurement_devices_test
 - Run InitializerTestingDB class in service/src/test/java/com/softserve/edu/config
 - Open database_script.sql and change name of database to measurement_devices_test
 - Run script
 - If the test, that you will try to run, have annotation @Ignore - remove it before running test
 - Run tests.


## Team members


### REST API


> TODO

#### LV-144 Java:
Експерт: Михайло Партика

Викладачі: Вікторія Ряжська та В’ячеслав Колдовський 

Учасники:
 - Михайло Осипов
 - Дмитро Добровольський
 - Олесь Онищак
 - Іван Романів 
 - Олег Чернигевич
 - Олександр Виблов
 
#### LV-150 Java:
Експерт: Микола Марчук

Викладач: Вікторія Ряжська

Учасники:
 - Михайло Коник
 - Оксана Михалець
 - Володимир Франів
 - Роман Чмелик
 - Максим Гірняк
 - Михайло Матвіїшин
 - Богдан Горох
 
#### LV-157 Java:
Експерт: Михайло Партика

Викладач: Вікторія Ряжська

Учасники:
 - Василь Чопик
 - Володимир Ігнатьєв
 - Володимир Назаркевич 
 - Богдан Конончук 
 - Дмитро Брилюк
 - Іван Циба 
 - Назарій Івашків
 - Назарій Мельничук
 - Олег Косар
 - Тарас Паничок
 

#### LV-165 Java:
Експерт: Назар Свідерський

Викладачі: Андрій Переймибіда та Любомир Галамага

Учасники:
 - Андрій Стефанський
 - Вероніка Герасименко
 - Любомир Лісень
 - Михайло Закалюжний
 - Павло Березін
 - Роман Горбовий
 - Софія Винницька
 - Юрій Дворник


#### LV-171 Java:
Експерт: Микола Марчук

Викладач: Вікторія Ряжська

Учасники:
 - Артем Коваль
 - Богдан Смачило
 - Ігор Соколик
 - Лідія Суботіна
 - Максим Марич
 - Руслан Башенський
 - Богдан Продан