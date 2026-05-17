package com.taw.polybank.service

import com.taw.polybank.dao.SuspiciousAccountRepository
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

/**
 * @author Lucía Gutiérrez Molina
 */
@Service
class SuspiciousAccountService(
    private val suspiciousAccountRepository: SuspiciousAccountRepository,
) {
    fun isSuspicious(bankAccountReceiverIBAN: String?): Boolean {
        val suspiciousAccountEntity =
            suspiciousAccountRepository.findByIban(bankAccountReceiverIBAN).orElse(null)
        return suspiciousAccountEntity != null
    }
}
