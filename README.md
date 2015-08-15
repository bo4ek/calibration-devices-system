
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

Before running the app, create MySQL database called `measurement_devices` with charset encoding UTF-8. 
Then run the following command on the root folder of the repository:

    mvn clean install tomcat7:run-war

 Once hibernate creates tables  use `database_script.sql` to fill data.

After the server starts, the application is accessible at the following URL:

    http://localhost:8080/
    
To get an admin page login with the following credentials:

    username: admin
    password: password
	
To get an provider page login with the following credentials:
	
	username: provider-lv
	password: pass

To get an provider page login with the following credentials:
	
	username: calibrator-lv
	password: pass	
	
To get an provider page login with the following credentials:
	
	username: verificator-lv
	password: pass
	
Check `database_script.sql` for additional information about other users such as provider, calibrator etc.

### Some overview

> TODO


### REST API


> TODO
=======
### Team members
##### LV-144 Java:
Експерт: Михайло Партика

Викладачі: Вікторія Ряжська та В’ячеслав Колдовський 

Учасники:
 - Михайло Осипов
 - Дмитро Добровольський
 - Олесь Онищак
 - Іван Романів 
 - Олег Чернигевич
 - Олександр Виблов
 
##### LV-150 Java:
Експерт: Микола Марчик

Викладач: Вікторія Ряжська

Учасники:
 - Михайло Коник
 - Оксана Михалець
 - Володимир Франів
 - Роман Чмелик
 - Максим Гірняк
 - Михайло Матвіїшин
 - Богдан Горох
 
### REST API



#### Checklist те, що Lv-157.Java має тестити

>Загальна стуктура

|service|
 |src|
  |main|
   |java|
    ...
   |service|
    |admin|
        |-OrganizationService.java
        |-StatisticService.java
        |-UsersService.java
    |calibrator|
        |-CalibratorEmployeeService.java
        |-CalibratorService.java
    |catalogue|
        |-BuildingService.java
        |-District.java
        |-LocalityService.java
        |-RegionService.java
        |-StreetService.java
        |-StreetTypeService.java
    |exceptions|
        |-NotAvailableException.java
    |provider|
        |buildingGraphic|
            |-GraphicBuilder.java
            |-GraphicBuilderMainPanel.java
            |-MonthOfYear.java
            |-ProviderEmployeeGraphic.java
        |-ProviderEmployeeService.java
        |-ProviderService.java
    |state.verification|
        |-StateVerificatorEmployeeService.java
        |-StateVerificatorService.java
    |storage|
        |impl|
            |-FileOperationImpl.java
            |-FileSearch.java
            |-SaveOptions.java
        |-FileOperations.java
    |utils|
        |-ArchivalVerificationsQueryConstructorCalibrator.java
        |-ArchivalVerificationsQueryConstructorProvider.java
        |-ArchivalVerificationsQueryConstructorVerificator.java
        |-CalibrationTestDataList.java
        |-CalibrationTestList.java
        |-DataDtoField.java
        |-EmployeeProvider.java
        |-ListToPageTransformer.java
        |-NewVerficationsQueryConstructorCalibrator.java
        |-NewVerficationsQueryConstructorProvider.java
        |-NewVerficationsQueryConstructorVerificator.java
        |-ProviderEmployeeGraphic.java
        |-ProviderEmployeeQuary.java
        |-TransformStrinsToMonths
    |verification|
        |-VerificationProviderEmployeeService.java
        |-VerificationService.java
    |-CalibrationTestDataService.java
    |-CalibrationTestService.java
    |-DeviceService.java
    |-DocumentsService.java
    |-MailService.java
    |-MeasuringEquipmentService.java
    |-SecurityUserDetailsService.java
    |-UserService.java
    |-VerificationPhotoService.java
    
>Ті класи які треба брати тестити

Хто вже взяв якісь видаляєте звідси і записуєте в "Список тих, хто робить і що робить" 

|service|
 |src|
  |main|
   |java|
    ...
   |service|
    |calibrator|
        |-CalibratorEmployeeService.java
        |-CalibratorService.java
    |catalogue|
        |-BuildingService.java
        |-District.java
        |-LocalityService.java
        |-RegionService.java
        |-StreetService.java
        |-StreetTypeService.java
    |exceptions|
        |-NotAvailableException.java
    |provider|
        |buildingGraphic|
            |-GraphicBuilder.java
            |-GraphicBuilderMainPanel.java
            |-MonthOfYear.java
            |-ProviderEmployeeGraphic.java
        |-ProviderEmployeeService.java
        |-ProviderService.java
    |state.verification|
        |-StateVerificatorEmployeeService.java
        |-StateVerificatorService.java
    |storage|
        |impl|
            |-FileOperationImpl.java
            |-FileSearch.java
            |-SaveOptions.java
        |-FileOperations.java
    |utils|
        |-ArchivalVerificationsQueryConstructorCalibrator.java
        |-ArchivalVerificationsQueryConstructorProvider.java
        |-ArchivalVerificationsQueryConstructorVerificator.java
        |-CalibrationTestDataList.java
        |-CalibrationTestList.java
        |-DataDtoField.java
        |-EmployeeProvider.java
        |-ListToPageTransformer.java
        |-NewVerficationsQueryConstructorCalibrator.java
        |-NewVerficationsQueryConstructorProvider.java
        |-NewVerficationsQueryConstructorVerificator.java
        |-ProviderEmployeeGraphic.java
        |-ProviderEmployeeQuary.java
        |-TransformStrinsToMonths
    |verification|
        |-VerificationProviderEmployeeService.java
        |-VerificationService.java
    |-CalibrationTestDataService.java
    |-CalibrationTestService.java
    |-DeviceService.java
    |-DocumentsService.java
    |-MailService.java
    |-MeasuringEquipmentService.java
    |-SecurityUserDetailsService.java
    |-UserService.java
    |-VerificationPhotoService.java

>Список тих, хто робить і що робить

1) Брилюк Д.А.
|service|
 |src|
  |main|
   |java|
    ...
   |service|
    |admin|
        |-OrganizationService.java
        |-StatisticService.java
        |-UsersService.java

>Те що зроблено

Сюди копіюйте, ті назви класів які ви завершили робити
### Назар Івашків
 - DeviceService
 - CalibrationTestService
