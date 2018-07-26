package wallet.kiwinam.charlie.kwtwallet.wallet.transaction

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import wallet.kiwinam.charlie.kwtwallet.R
import java.text.DecimalFormat

class TransactionAdapter(private var transactionList : ArrayList<Transaction>,val context : Context) : RecyclerView.Adapter<TransactionAdapter.TransactionHolder>(){
    private val decimalFormat = DecimalFormat("#,##0")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction,parent,false)
        return TransactionHolder(v)
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val transaction = transactionList[position]

        // 0 < 보내기, 1 < 받기 , 2 < 토큰 요청
        when {
            transaction.state == 0 -> {         // 내가 보낸 트랜잭션이라면
                holder.tranStateIv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_send_white_24dp))
                holder.tranStateIv.setBackgroundResource(R.drawable.sp_state_send)
                holder.tranStateTv.text = "보냄"
            }
            transaction.state == 1 -> {   // 내가 받은 트랜잭션이라면
                holder.tranStateIv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_reply_white_24dp))
                holder.tranStateIv.setBackgroundResource(R.drawable.sp_state_send)
                holder.tranStateTv.text = "받음"
            }
            else -> {      // 내가 토큰을 요청한 것이라면
                holder.tranStateIv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add_white_24dp))
                holder.tranStateIv.setBackgroundResource(R.drawable.sp_state_send)
                holder.tranStateTv.text = "충전"
            }
        }
        holder.tranFromTv.text = transaction.from
        holder.tranToTv.text = transaction.to
        holder.tranQuantityTv.text = decimalFormat.format(transaction.Quantity) + " KWT"
    }

    fun setTransactionList(transactionList: ArrayList<Transaction>){
        this.transactionList = transactionList
        notifyDataSetChanged()
    }

    class TransactionHolder (transactionHolder : View ): RecyclerView.ViewHolder(transactionHolder){
        val tranStateIv = transactionHolder.findViewById<ImageView>(R.id.tranStateIv)!!

        val tranFromTv = transactionHolder.findViewById<TextView>(R.id.tranFromTv)!!
        val tranToTv = transactionHolder.findViewById<TextView>(R.id.tranToTv)!!
        val tranQuantityTv = transactionHolder.findViewById<TextView>(R.id.tranQuantityTv)!!
        val tranStateTv = transactionHolder.findViewById<TextView>(R.id.tranStateTv)!!
    }
}