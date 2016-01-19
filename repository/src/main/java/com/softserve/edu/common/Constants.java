package com.softserve.edu.common;

public interface Constants {

    double CONVERT = 3.6;
    int MIN_LENGTH = 3;
    int PERCENT = 100;
    int SCALE_2 = 2;
    int SCALE_3 = 3;
    int TEST_COUNT = 6;
    String MAIN_PHOTO = "mainPhoto";
    String BEGIN_PHOTO = "beginPhoto";
    String END_PHOTO = "endPhoto";
    String DOT = ".";
    String TEST_OK = "придатний";
    String TEST_NOK = "не придатний";
    String COUNT_ACCEPTED_VER = "Кількість прийнятих заявок";
    String COUNT_REJECTED_VER = "Кількість відхилених заявок";
    String COUNT_ALL_VERIFICATIONS = "Кількість виконаних заявок, всього";
    String COUNT_OK_VERIFICATIONS = "Кількість виконаних заявок з результатом «придатний»";
    String COUNT_NOK_VERIFICATIONS = "Кількість виконаних заявок з результатом «не придатний»";
    String CALIBRATOR_ORGANIZATION_NAME = "Назва вимірювальної лабораторії";
    String NUMBER_IN_SEQUENCE_SHORT = "№ з/п";
    String CUSTOMER_ADDRESS = "Адреса замовника";
    String DEVICE_TYPE_YEAR = "Тип приладу, рік випуску";
    String DIAMETER = "Діаметр";
    String MEASURING_LAB_NAME = "Назва вимірювальної лабораторії";
    String RESULT = "Результат";
    String DOCUMENT_DATE = "Дата документа";
    String DOCUMENT_NUMBER = "№ документа";
    String VALID_UNTIL = "Придатний до";
    String COUNTERS_NUMBER = "Кількість лічильників";
    String PROVIDER = "Надавач послуг";
    String VERIFICATION_ID = "Номер повірки";
    String COUNTER_NUMBER = "Номер лічильника";
    String COUNTER_TYPE = "Тип лічильника";
    String COUNTER_SIZE_AND_SYMBOL = "Розмір і символ лічильника";
    String YEAR = "Рік випуску лічильника";
    String STAMP = "Номер пломби";
    String DEFAULT_DB_TABLE_NAME = "Subscribers";

    //reasons o counter unusability
    String MEASURING_ERROR_MESSAGE = "відносна похибка вимірювання об’єму перевищує межі допустимих значень за ";
    String RATED_FLAW = "номінальною витратою";
    String TRANSIENT_FLAW = "перехідною витратою";
    String MINIMAL_FLAW = "мінімальною витратою";
    String NOT_SPECIFIED = "не вказано";

    // region Address details

    String CITY = "Місто";
    String CITY_ID = "Id міста";
    String REGION = "Район";
    String DISTRICT_ID = "Id району";
    String ADDRESS = "Адреса";
    String BUILDING = "Будинок";
    String FLAT = "Квартира";
    String ENTRANCE = "Під'їзд";
    String FLOOR = "Поверх";
    String STREET = "Вулиця";
    String STREET_ID = "Id вулиці";

    // endregion

    String COMMENT = "Примітка";

    // region Personal info

    String CUSTOMER_ID = "Id замовника";
    String FULL_NAME_SHORT = "ПІБ";
    String PHONE_NUMBER = "Телефон";
    String FULL_NAME_CUSTOMER = "ПІБ замовника";
    String FULL_NAME = "ПІБ працівника";
    String FIRST_NAME = "Ім'я";
    String LAST_NAME = "Прізвище";
    String MIDDLE_NAME = "По батькові";

    // endregion

    // region File extensions and names

    String XLS_EXTENSION = "xls";
    String ZIP_EXTENSION = "zip";
    String DB_EXTENSION = "db";
    String IMAGE_TYPE = "jpg";

    // endregion

    // region Task

    String TASK = "Завдання";
    String TASK_DATE = "Дата завдання";
    String DESIRABLE_TIME = "Бажаний час";

    // endregion

    // region Date Format

    String YEAR_MONTH_DAY = "yyyy-MM-dd";
    String DAY_MONTH_YEAR = "ddMMyyyy";
    String DAY_FULL_MONTH_YEAR = "dd MMMMM yyyy";
    String FULL_DATE = "dd.MM.yyyy HH:mm:ss";
    String DATE = "Дата";

    // endregion
}
