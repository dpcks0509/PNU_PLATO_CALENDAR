package pusan.university.plato_calendar.data.remote.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CafeteriaService {
    @FormUrlEncoded
    @POST("/kor/CMS/MenuMgr/menuListOnBuilding.do")
    suspend fun readCafeteriaMenusOnBuilding(
        @Field("mCode") mCode: String = "MN202",
        @Field("campus_gb") campusGb: String,
        @Field("building_gb") buildingGb: String,
        @Field("restaurant_code") restaurantCode: String,
        @Field("menu_date") menuDate: String = "",
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/kor/CMS/MenuMgr/menuListOnWeekly.do")
    suspend fun readCafeteriaMenusOnWeekly(
        @Field("mCode") mCode: String = "MN202",
        @Field("campus_gb") campusGb: String,
        @Field("menu_date") menuDate: String = "",
    ): Response<ResponseBody>
}

// 'https://www.pusan.ac.kr/kor/CMS/MenuMgr/menuListOnBuilding.do
// ?mCode=MN202&campus_gb=PUSAN&building_gb=R001&restaurant_code=PG002';

// https://www.pusan.ac.kr/kor/CMS/MenuMgr/menuListOnWeekly.do?mCode=MN203
// ?mCode=MN203&campus_gb=PUSAN&menu_date=2026-01-28';
