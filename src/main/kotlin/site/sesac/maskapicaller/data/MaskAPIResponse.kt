package site.sesac.maskapicaller.data

import com.fasterxml.jackson.annotation.JsonProperty

class MaskAPIResponse(
    @JsonProperty("data")
    val data: Data
)
data class Data(
    @JsonProperty("result_b64")
    val resultB64: String
)