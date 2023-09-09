package site.sesac.maskapicaller.data

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.*


@Entity
@Table(name = "users")
data class Picture(

    @Id
    var id: Int = 0,

    @Column(name = "EMAIL")
    var userMail: String,

    @Column(name = "S3_URL")
    var pictureUrl: String,

    @Column(name = "STATUS")
    var jobStat : String,

    @Column(name = "CREATEDAT")
    var  createdAt : Date,

    @Column(name ="UPDATEDAT")
    var  updatedAt: LocalDateTime


)
