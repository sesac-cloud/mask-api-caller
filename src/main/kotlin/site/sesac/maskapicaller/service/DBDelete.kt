package site.sesac.maskapicaller.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import site.sesac.maskapicaller.repo.PictureDelete

@Service
class DBDelete(private val pictureDel: PictureDelete) {
    @Transactional
    fun deleteOnFail(email: String) : Boolean {

        return if (pictureDel.findByUserMailAndJobStat(email,"N") != null) {
            pictureDel.deleteOnFail(email)
            true
        } else{
            false
        }
    }
}