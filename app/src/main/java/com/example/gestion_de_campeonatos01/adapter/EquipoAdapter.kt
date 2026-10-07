package com.example.gestion_de_campeonatos01.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestion_de_campeonatos01.R
import com.example.gestion_de_campeonatos01.data.Equipo

class EquipoAdapter : RecyclerView.Adapter<EquipoAdapter.EquipoViewHolder>() {

    private var lista = emptyList<Equipo>()

    fun actualizarLista(nuevaLista: List<Equipo>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_equipo, parent, false)
        return EquipoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EquipoViewHolder, position: Int) {
        val equipo = lista[position]
        holder.txtNombre.text = equipo.nombre
        val imagen = equipo.imagen
        val bitmap = imagen?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
        if (bitmap != null) {
            holder.imgEquipo.setImageBitmap(bitmap)
        } else {
            // Reinicia el icono al reutilizar una fila sin imagen.
            holder.imgEquipo.setImageResource(R.drawable.ic_equipo)
        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    class EquipoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombreEquipo)
        val imgEquipo: ImageView = itemView.findViewById(R.id.imgEquipo)
    }
}
