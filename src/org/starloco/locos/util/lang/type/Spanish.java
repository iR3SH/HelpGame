package org.starloco.locos.util.lang.type;

import org.starloco.locos.client.Player;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.util.lang.AbstractLang;

/**
 * Created by Locos on 09/12/2015.
 */
public class Spanish extends AbstractLang {

    public final static Spanish singleton = new Spanish();

    public static Spanish getInstance() {
        return singleton;
    }

    public void initialize() {
        int index = 0;
        this.sentences.add(index, "El canal general está desactivado."); index++;
        this.sentences.add(index, "Algunos caracteres usados en tu sentencia están deshabilitados."); index++;
        this.sentences.add(index, "Debes esperar #1 segundo(s)."); index++;
        this.sentences.add(index, "Se ha activado el canal general."); index++;
        this.sentences.add(index, "Se ha desactivado el canal general."); index++;
        this.sentences.add(index, "Lista del Staff conectado:"); index++;
        this.sentences.add(index, "No hay ningún miembro del staff conectado."); index++;
        this.sentences.add(index, "No estás bugeado..."); index++;
        this.sentences.add(index, "<b>" + Config.getInstance().NAME + "</b>\nOnline desde : #1D #2H #3M #4S."); index++;
        this.sentences.add(index, "\nJugadores online : #1"); index++;
        this.sentences.add(index, "\nJugadores conectados : #1"); index++;
        this.sentences.add(index, "\nMayoría en línea : #1"); index++;
        Player player = null;
        this.sentences.add(index, "Los <b>comandos</b> disponibles son: \n"
               // + "<b>.infos</b> - Informate sobre el servidor.\n"
                + "<b>.deblo</b> - Te teletransporta a una celda libre.\n"
                + "<b>.staff</b> - Mira los miembros del staff online.\n"
               // + "<b>.all</b>   - Envía un mensaje a todos los jugadores. (Ej: .all ¡Hola!)\n"
               // + "<b>.noall</b> - No te permite recibir mensajes de todos.\n"
               // + "<b>.level</b> - Puedes elegir tu nivel. (Ej : .level 100)\n"
                + "<b>.restat</b> - Permite reiniciar tus stats a 0.\n"
                + "<b>.parcho</b> - Te scrollea a todos los elementos a 101.\n"
                + "<b>.boost</b> - Sube tus puntos de stats fácil (Ej: .subir vida/sabi/fo/inte/suerte/agi 335). \n"
                + "<b>.spellmax</b> - Sube tus hechizos a nivel Máximo.\n"
                + "<b>.jetmax</b> - Perfecciona tus items (Ej: .jetmax all)\n"
                + "<b>.exo</b> - Maguea tu set con un exo PA o PM. (Ej: .exo cape pa)\n"
                + "<b>.fmcac</b> - Maguea tu arma a un elemento. (Ej: .fmcac feu)\n"
                + "<b>.start</b> - Ve al mapa principal y shop.\n"
                + "<b>.poutch</b> - Prueba tus hechizos con un Puch Ingball.\n" 
                + "<b>.maitre, .tp</b> - Has un grupo con tus personajes. Y con tp teleporta a tu equipo\n"
               // + "<b>.tp</b> - Teleporta a tu equipo.\n"
                + "<b>.pvm</b> - Ve al mapa de PvM.\n"
                + "<b>.vie</b> - Restaura tu vida.\n"
                + "<b>.banque</b> - ¡Accede a tu banco donde estés! \n"
                + "<b>.transfert</b> - Te permite transferir tus recursos al banco (Latencias de 30 artículos.)\n"
                + "<b>.enclos</b> - Te llevará al cercado. \n"
                + "<b>.phoenix</b> - Te teleporta al Fénix.\n"
                + "<b>.ange</b> - Te permite ser Bontariano\n"
                + "<b>.demon</b> - Te permite ser Brakmariano.\n"
                + "<b>.neutre</b> - Te permite ser neutral.\n"); index++;
        this.sentences.add(index, "Para ver los comandos, usa .comandos ¡Te deseamos un buen juego!.");
      // this.sentences.add(index, "Ahora puedes votar, pulsando <b><a href='" + Config.getInstance().url + "'>aquí­</a></b>.");
    }
}
