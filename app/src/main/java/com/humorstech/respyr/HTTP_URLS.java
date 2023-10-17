package com.humorstech.respyr;

public class HTTP_URLS {

    public static String checkPhoneNumber = "https://humorstech.com/humors_app/app_final/check_phone_number.php?";
    //https://humorstech.com/humors_app/app_final/check_phone_number.php?phone_no=5451&country_code=91$IN

    public static String insertNewProfile = "https://humorstech.com/humors_app/app_final/add_new_profile.php?";
    //https://humorstech.com/humors_app/app_final/add_new_profile.php?login_id=01&phone=1234567890&name
    // =Johgfvhje&email=johndoe%40example.com&gender=
    // Male&dob=1990-01-01&age=30&height=180&weight=75&water_consumption=2000&bmi=24.3

    public static String checkEmailExist = "https://humorstech.com/humors_app/app_final/check_email_exist.php?";
    //https://humorstech.com/humors_app/app_final/check_email_exist.php?email=c

    public static String checkNameExist = "https://humorstech.com/humors_app/app_final/check_name_exist.php?";
    //https://humorstech.com/humors_app/app_final/check_email_exist.php?email=c

    public static String addBloodReport = "https://humorstech.com/humors_app/app_final/add_blood_report.php?";
    //https://humorstech.com/humors_app/app_final/add_blood_report.php?id=123&
    // login_id=user123&profile_id=profile456&diabetic=true&diabetic_values=1,3&dttm=2023-05-30

    public static String addHobbies = "https://humorstech.com/humors_app/app_final/add_hobbies.php?";

    public static String sendOTP = "https://humorstech.com/humors_app/app_final/send_otp.php?";
    //https://humorstech.com/humors_app/app_final/send_otp.php?phone_no=9902552385&country_code=91$IN

    public static String checkOTP = "https://humorstech.com/humors_app/app_final/check_otp.php?";
    //https://humorstech.com/humors_app/app_final/check_otp.php?phone_no=9902552385&country_code=91$IN&otp=671

    public static String checkProfileCount = "https://humorstech.com/humors_app/app_final/fetch_profile_counts.php?";
    //https://humorstech.com/humors_app/app_final/fetch_profile_counts.php?login_id=02--profile count

    public static String fetchProfilesOfLogin = "https://humorstech.com/humors_app/app_final/select_profile.php?";
    //https://humorstech.com/humors_app/app_final/select_profile.php?login_id=RESPYR001

    public static String fetchProfileDataByProfileId="https://humorstech.com/humors_app/app_final/fetch_profile_data.php";
    //https://humorstech.com/humors_app/app_final/fetch_profile_data.php?login_id=RESPYR001&profile_id=profile1


    public static String insertDailyRoutine = "https://humorstech.com/humors_app/app_final/insert_daily_routine_data.php?";
           // "insert_data.php?id=1&login_id=1234&profile_id=5678&dttm=2023-07-24%2012:00:00&water_consumed=200&cigarettes_unit=5&alcohol_unit=2.5&exercise_minutes=60&sleep_hours=8&
    // food_intake=1&food_name=Pizza&food_quantity=2&skip_meal=0"



    ////
    public static String RAW_DATA_SAVE= "https://humorstech.com/humorscalculation/production.php?";


    //////////////////// RESULT PAGE SCORE
    public static String GENERATE_OVERALL_SCORE = "https://humorstech.com/humors/json_curl/life_style.php?";

    public static String ACTIVITY_SCORE_DATA = "https://humorstech.com/humors_app/app_final/trends/nutrition_trend.php?";
    public static String WATER_INTAKE_TREND_DATA = "https://humorstech.com/humors_app/app_final/trends/water_intake_trend.php?";


    /// TREND PAGE


    // end_date=09/16/2023&login_id=log123&profile_id=prof123&start_date=09/10/2023
    public static String DAILY_TREND = "https://humorstech.com/humors_app/app_final/trends/test4.php?";


    public static String DASHBOARD_DATA_DAY_WISE = "https://humorstech.com/humors_app/app_final/fetch_dashborad_data_daily.php?";
    //https://humorstech.com/humors_app/app_final/fetch_dashborad_data_daily.php?login_id=RESPYR037&profile_id=profile1&date=2023-09-18



    /// Account settings
    public static  String UPDATE_ACCOUNT_STATUS = "https://humorstech.com/humors_app/app_final/update_account_status.php?";
    //login_id=RESPYR039&status=inactive

    public static  String FETCH_TIMELINE_DATA_BY_ID = "https://humorstech.com/humors_app/app_final/fetch_timeline_data_by_id.php?";
    //https://humorstech.com/humors_app/app_final/fetch_timeline_data_by_id.php?id=36



    //// Update profile
    public static String UPDATE_PERSONAL_INFORMATION = "https://humorstech.com/humors_app/app_final/update_persnoal_info.php?";
    //https://humorstech.com/humors_app/app_final/update_persnoal_info.php?login_id=RESPYR001&profile_id=profile1&gender
    // =Male&dob=1990-01-01&age=30&height=175&weight=120&water_consumption=2000&bmi=22.8

    public static String UPDATE_HOBBIES_DATA = "https://humorstech.com/humors_app/app_final/update_hobbies_data.php?";
    //https://humorstech.com/humors_app/app_final/update_hobbies_data.php?login_id=RESPYR001&'
    // profile_id=profile1&water_consume=updated_value&alcoholic=20&smoking=updated_value&non_veg=
    // updated_value&exercise=updated_value&sleep_hours=updated_value&mental_conditions=updated_
    // value&life_stytle_score=updated_value

    public static  String UPDATE_SLEEP_ROUTINE = "https://humorstech.com/humors_app/app_final/update_sleep_routine.php?";
    //https://humorstech.com/humors_app/app_final/update_sleep_routine.php?login_id=RESPYR009&profile_id=profile1&sleep_hours=10


    public static  String UPDATE_MENTAL_CONDITIONS = "https://humorstech.com/humors_app/app_final/update_mental_state.php?";
    //login_id=RESPYR009&profile_id=profile1&mental_state=10
    public static  String UPDATE_MEDICAL_CONDITIONS = "https://humorstech.com/humors_app/app_final/update_medical_history.php?";
    //login_id=RESPYR041&conditions=guyhg&profile_id=profile1


    public static String GET_DB_VITAL = "https://humorstech.com/humors/json_curl/db_vital.php?";



    //////////////////Dashboard Data
    public static String GET_DASHBOARD_DATA_BY_DATE = "https://humorstech.com/humors_app/app_final/get_dashboard_data_by_date.php?";
    //https://humorstech.com/humors_app/app_final/get_dashboard_data_by_date.php?login_id=RESPYR003&profile_id=profile1&date=2023-10-01

    public  static  String  GET_DASHBOARD_TREND_DATA_BY_DATE = "https://humorstech.com/humors_app/app_final/trends/dashboard_trend_by_date.php?";
    //https://humorstech.com/humors_app/app_final/trends/dashboard_trend_by_date.php?login_id=RESPYR003&profile_id=profile1&date=2023-10-02


    public  static  String  DAILY_TREND_DASHBOARD = "https://humorstech.com/humors_app/app_final/trends/current_day_trend.php?";

}
