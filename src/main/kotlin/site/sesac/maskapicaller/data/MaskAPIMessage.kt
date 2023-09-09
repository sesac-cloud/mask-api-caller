package site.sesac.maskapicaller.data

import com.fasterxml.jackson.annotation.JsonProperty

data class MaskAPIMessage(
    @JsonProperty("bgPrompt")
    var prompt: String ,
    @JsonProperty("userMail")
    var userMail : String ,
    @JsonProperty("originPhoto")
    var originPhoto : String

)
