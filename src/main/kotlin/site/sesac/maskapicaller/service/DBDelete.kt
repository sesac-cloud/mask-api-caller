package site.sesac.maskapicaller.service

import org.springframework.stereotype.Service
import site.sesac.maskapicaller.repo.PictureDelete

@Service
class DBDelete(private val pictureDel: PictureDelete) {
    fun deleteOnFail(email: String) {
        pictureDel.deleteOnFail(email)
    }
}