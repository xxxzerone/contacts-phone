package com.example.contactphone.data.source

import com.example.contactphone.domain.model.Contact
import javax.inject.Inject

class MockContactDataSource @Inject constructor() {
    fun getMockContacts(): List<Contact> {
        return listOf(
            Contact("mock_1", "홍길동", "010-1234-5678", null),
            Contact("mock_2", "김철수", "010-1111-2222", null),
            Contact("mock_3", "이영희", "010-3333-4444", null),
            Contact("mock_4", "박지성", "010-5555-6666", null),
            Contact("mock_5", "손흥민", "010-7777-8888", null),
            Contact("mock_6", "김연아", "010-9999-0000", null),
            Contact("mock_7", "최동원", "010-1212-3434", null),
            Contact("mock_8", "나건우", "010-4321-8765", null),
            Contact("mock_9", "다비치", "010-0000-0000", null),
            Contact("mock_10", "라미란", "010-1111-1111", null)
        )
    }
}
