package pusan.university.plato_calendar.data.remote.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CafeteriaService {
    @FormUrlEncoded
    @POST("/kor/CMS/MenuMgr/menuListOnBuilding.do")
    suspend fun getCafeteriaWeeklyPlan(
        @Field("mCode") mCode: String = "MN202",
        @Field("campus_gb") campus: String,
        @Field("building_gb") buildingCode: String,
        @Field("restaurant_code") restaurantCode: String,
        @Field("menu_date") menuDate: String = "",
    ): Response<ResponseBody>
}
