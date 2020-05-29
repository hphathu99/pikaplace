package thu.ait.pikaplaceapplication.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import thu.ait.pikaplaceapplication.data.PlacesResult

/*
https://api.foursquare.com/v2/venues/search?client_id=HPTY2KNYXWKQMUIWB5DEZQRL1EJC2VH2LSCKWPZOCFSEIKLZ&client_secret=YEQMSQMLCWWPGJMK2ZR1BVBO15GR0CDI2J5YVJWNN11S2YMW&v=20180323&radius=1000&ll=40.7128,-74.0060&categoryId=4d4b7105d754a06374d81259&limit=4
 */
interface PlacesAPI {
    // host: https://api.foursquare.com/
    // path: /v2/venues/explore

    @GET("/v2/venues/search")
    fun getPlaces(
        @Query("client_id") clienId: String,
        @Query("client_secret") clientSecret: String,
        @Query("v") version: String,
        @Query("ll") longlat: String,
        @Query("categoryId") cateId: String,
        @Query("radius") radius: Int,
        @Query("limit") limit: Int
    ): Call<PlacesResult>
}