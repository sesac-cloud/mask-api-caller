package site.sesac.maskapicaller.repo

import jakarta.persistence.OneToOne
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import site.sesac.maskapicaller.data.Picture

interface PictureDelete : JpaRepository<Picture, Int> {
    @Modifying
    @Transactional
    @OneToOne(targetEntity = Picture::class)
    @Query("DELETE Picture u WHERE u.userMail = :email AND u.jobStat = 'N'")
    fun deleteOnFail(email: String)
    fun findByUserMailAndJobStat(userMail: String, status: String): Picture?

}