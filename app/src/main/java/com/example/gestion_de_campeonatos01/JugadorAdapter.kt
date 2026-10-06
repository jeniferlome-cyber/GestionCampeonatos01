package com.example.gestion_de_campeonatos01.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestion_de_campeonatos01.R
import com.example.gestion_de_campeonatos01.data.Jugador

class JugadorAdapter(

    private val onEliminar: (Jugador) -> Unit,
    private val onModificar: (Jugador) -> Unit

) : RecyclerView.Adapter<JugadorAdapter.JugadorViewHolder>() {

    private var lista = emptyList<Jugador>()

    fun actualizarLista(nuevaLista: List<Jugador>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JugadorViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jugador, parent, false)

        return JugadorViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: JugadorViewHolder,
        position: Int
    ) {
        val jugador = lista[position]

        holder.txtNombre.text = jugador.nombre
        holder.txtDni.text = "DNI: ${jugador.dni}"
        holder.txtEdad.text = "Edad: ${jugador.edad} años"
        holder.txtEquipo.text = "Equipo: ${jugador.equipo}"

        holder.btnEliminar.setOnClickListener {
            onEliminar(jugador)
        }

        holder.btnModificar.setOnClickListener {
            onModificar(jugador)
        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    class JugadorViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val txtNombre: TextView =
            itemView.findViewById(R.id.txtNombre)

        val txtDni: TextView =
            itemView.findViewById(R.id.txtDni)

        val txtEdad: TextView =
            itemView.findViewById(R.id.txtEdad)

        val txtEquipo: TextView =
            itemView.findViewById(R.id.txtEquipo)

        val btnEliminar: Button =
            itemView.findViewById(R.id.btnEliminar)

        val btnModificar: Button =
            itemView.findViewById(R.id.btnModificar)
    }
}