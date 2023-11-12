package com.msprysak.rentersapp.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.adapters.ChatAdapter
import com.msprysak.rentersapp.databinding.FragmentChatBinding

class ChatFragment : BaseFragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val chatViewModel by viewModels<ChatViewModel>()

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val messageEditText = binding.messageEditText

        recyclerView = binding.chatRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        chatViewModel.fetchMessages().observe(viewLifecycleOwner) { messages ->
            chatAdapter = ChatAdapter(messages)
            recyclerView.adapter = chatAdapter
            recyclerView.scrollToPosition(messages.size - 1)
            chatAdapter.notifyDataSetChanged()
        }




        binding.messageSendButton.setOnClickListener {
            if (messageEditText.text.isNotEmpty()){
                sendMessage(messageEditText.text.toString())
            }
        }

    }

    private fun sendMessage(message: String) {
        if (message.isNotEmpty()) {
            chatViewModel.sendMessage(message)
            binding.messageEditText.text.clear()
        }
    }

}