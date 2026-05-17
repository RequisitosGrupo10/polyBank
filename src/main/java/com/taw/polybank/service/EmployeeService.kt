package com.taw.polybank.service

import com.taw.polybank.dao.BankAccountRepository
import com.taw.polybank.dao.EmployeeRepository
import com.taw.polybank.dao.RequestRepository
import com.taw.polybank.dto.EmployeeDTO
import com.taw.polybank.dto.RequestDTO
import com.taw.polybank.entity.EmployeeEntity
import com.taw.polybank.entity.RequestEntity
import org.springframework.stereotype.Service
import java.util.function.ToIntFunction

/**
 * @author Illya Rozumovskyy 20%
 * @author Javier Jordán Luque 40%
 * @author José Manuel Sánchez Rico 40%
 */
@Service
class EmployeeService(
    private val employeeRepository: EmployeeRepository,
    private val requestRepository: RequestRepository,
    private val bankAccountRepository: BankAccountRepository,
) {
    fun solveRequest(
        id: Int,
        approved: Boolean,
    ) {
        val requestEntityOptional = requestRepository.findById(id)
        if (requestEntityOptional.isEmpty()) return
        val requestEntity = requestEntityOptional.get()
        requestEntity.setApproved(approved)
        requestEntity.setSolved(true)
        val bankAccountEntity = requestEntity.getBankAccountByBankAccountId()
        if (approved) bankAccountEntity.setActive(true)
        requestRepository.save<RequestEntity>(requestEntity)
        bankAccountRepository.save(bankAccountEntity)
    }

    fun findRequestsForEmployee(employee: EmployeeEntity?): MutableList<RequestDTO?>? {
        if (employee != null && employee.getType().toString() == "manager") {
            return getDtoList(requestRepository.findBySolvedAndAndEmployeeByEmployeeId(false, employee))
        }
        return ArrayList()
    }

    fun getDtoList(requestEntityList: MutableList<RequestEntity>): MutableList<RequestDTO?> {
        val requestDTOS: MutableList<RequestDTO?> = ArrayList()
        for (requestEntity in requestEntityList) requestDTOS.add(requestEntity.toDTO())
        return requestDTOS
    }

    fun findById(employeeId: Int): EmployeeDTO? {
        val employeeEntity = this.employeeRepository.findById(employeeId).orElse(null)
        if (employeeEntity != null) {
            return employeeEntity.toDTO()
        }
        return null
    }

    fun findEmployeeWithMinimumChats(): List<EmployeeDTO?> = this.listToDTO(employeeRepository.findEmployeeWithMinimumChats())

    protected fun listToDTO(employeeEntityList: List<EmployeeEntity>): List<EmployeeDTO?> {
        val employeeList: ArrayList<EmployeeDTO> = ArrayList()
        for (employeeEntity in employeeEntityList) {
            employeeList.add(employeeEntity.toDTO())
        }
        return employeeList
    }

    fun findManager(): EmployeeDTO? {
        val manager =
            employeeRepository
                .findAllManagers(EmployeeEntity.EmployeeType.MANAGER)
                .stream()
                .min(Comparator.comparingInt(ToIntFunction { mgr: EmployeeEntity -> mgr.requestsById.size }))
                .orElse(null)
        return if (manager == null) null else manager.toDTO()
    }

    fun toEntity(employee: EmployeeDTO): EmployeeEntity {
        var employeeEntity = employeeRepository.findById(employee.getId()).orElse(null)
        if (employeeEntity == null) {
            employeeEntity = EmployeeEntity()
        }
        employeeEntity.setId(employee.getId())
        employeeEntity.setDni(employee.getDni())
        employeeEntity.setName(employee.getName())
        employeeEntity.setType(employee.getType())
        return employeeEntity
    }
}
