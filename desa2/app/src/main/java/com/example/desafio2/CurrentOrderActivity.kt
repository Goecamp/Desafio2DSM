package com.example.desafio2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CurrentOrderActivity : AppCompatActivity() {
    private lateinit var recyclerViewOrder: RecyclerView
    private lateinit var tvOrderTotal: TextView
    private lateinit var btnConfirmOrder: Button
    private lateinit var dbHelper: DatabaseHelper
    private val usuarioId: Int = 1 // Suponiendo que el ID de usuario es 1 (debería obtenerse dinámicamente)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_order)

        recyclerViewOrder = findViewById(R.id.recyclerViewOrder)
        tvOrderTotal = findViewById(R.id.tvOrderTotal)
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder)

        dbHelper = DatabaseHelper(this)

        val selectedItems = intent.getParcelableArrayListExtra<MenuItem>("selected_items") ?: arrayListOf()
        val total = intent.getDoubleExtra("total", 0.0)

        val adapter = CurrentOrderAdapter(selectedItems)
        recyclerViewOrder.adapter = adapter
        recyclerViewOrder.layoutManager = LinearLayoutManager(this)

        tvOrderTotal.text = String.format("Total: $%.2f", total)

        btnConfirmOrder.setOnClickListener {
            saveOrder(selectedItems, total)
            Toast.makeText(this, "Compra confirmada", Toast.LENGTH_SHORT).show()
            showNotification("Compra Confirmada", "Tu orden por $${String.format("%.2f", total)} ha sido confirmada")
            finish()
        }
    }

    private fun saveOrder(items: List<MenuItem>, total: Double) {
        // Obtener la fecha actual
        val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Guardar la orden en la base de datos
        dbHelper.savePurchase(usuarioId, fecha, total)
    }

    private fun showNotification(title: String, content: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("order_channel", "Order Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, "order_channel")
            .setSmallIcon(R.drawable.ic_notification) // Asegúrate de tener este icono en tus recursos
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(1, builder.build())
    }
}
