package cat.copernic.amayo.frontend.core.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Fàbrica personalitzada per crear instàncies de [SessionViewModel] amb dependències injectades.
 *
 * Permet instanciar el ViewModel amb un [SessionRepository] proporcionat, seguint el patró d'injecció
 * de dependències en entorns on no es fa servir Hilt o Dagger.
 *
 * @property sessionRepository Repositori utilitzat pel [SessionViewModel] per gestionar la sessió d'usuari.
 */
class ViewModelFactory(private val sessionRepository: SessionRepository) : ViewModelProvider.Factory {
    /**
     * Crea una nova instància de [SessionViewModel] quan és sol·licitada pel ViewModelProvider.
     *
     * @param modelClass Classe del ViewModel sol·licitat.
     * @return Instància de [SessionViewModel] inicialitzada amb el seu [SessionRepository].
     * @throws IllegalArgumentException si la classe no és compatible amb [SessionViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SessionViewModel(sessionRepository) as T
    }
}