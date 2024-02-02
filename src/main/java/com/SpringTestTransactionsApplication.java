package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.IllegalTransactionStateException;

import com.entity.Salume;
import com.exception.CustomCheckedException1;
import com.exception.CustomUncheckedException1;
import com.exception.CustomUncheckedException2;
import com.repository.SalumeRepository;
import com.service.SalumeService;

@SpringBootApplication
public class SpringTestTransactionsApplication implements CommandLineRunner {

	@Autowired
	private SalumeService service;

	@Autowired
	private SalumeRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(SpringTestTransactionsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		service.resetStatus();

		try {
			service.testThrowCheckedException();
		} catch (Exception e) {
			boolean result = repository.findAll().size() == 3;
			System.err.println("testThrowCheckedException() OK? " + result);
		}

		try {
			service.testThrowUncheckedException();
		} catch (RuntimeException rte) {
			boolean result = repository.findAll().size() == 3;
			System.err.println("testThrowUncheckedException() OK? " + result);
		}

		Boolean isRequiredNewWorking = service.testPropagationRequiredNew1();
		if (isRequiredNewWorking) {
			System.err.println("@Transactional(propagation = Propagation.REQUIRED_NEW1) -> OK");
		} else {
			System.err.println("@Transactional(propagation = Propagation.REQUIRED_NEW1) -> KO");
		}

		isRequiredNewWorking = service.testPropagationRequiredNew2();
		if (isRequiredNewWorking) {
			System.err.println("@Transactional(propagation = Propagation.REQUIRED_NEW2) -> OK");
		} else {
			System.err.println("@Transactional(propagation = Propagation.REQUIRED_NEW2) -> KO");
		}

		try {
			service.testPropagationMandatory1();
			System.err.println("@Transactional(propagation = Propagation.MANDATORY1) -> KO");
		} catch (IllegalTransactionStateException e) {
			System.err.println("@Transactional(propagation = Propagation.MANDATORY1) -> OK");
		}

		try {
			service.testPropagationMandatory2();
			System.err.println("@Transactional(propagation = Propagation.MANDATORY2) -> OK");
		} catch (IllegalTransactionStateException e) {
			System.err.println("@Transactional(propagation = Propagation.MANDATORY2) -> KO");
		}

		try {
			service.testPropagationMandatory3();
			System.err.println("@Transactional(propagation = Propagation.MANDATORY3) -> KO");
		} catch (IllegalTransactionStateException e) {
			System.err.println("@Transactional(propagation = Propagation.MANDATORY3) -> OK");
		}

		try {
			service.testPropagationNever1();
			System.err.println("@Transactional(propagation = Propagation.NEVER1) -> OK");
		} catch (IllegalTransactionStateException e) {
			System.err.println("@Transactional(propagation = Propagation.NEVER1) -> KO");
		}

		try {
			service.testPropagationNever2();
			System.err.println("@Transactional(propagation = Propagation.NEVER2) -> OK");
		} catch (IllegalTransactionStateException e) {
			System.err.println("@Transactional(propagation = Propagation.NEVER2) -> KO");
		}

		try {
			service.testPropagationNever3();
			System.err.println("@Transactional(propagation = Propagation.NEVER3) -> KO");
		} catch (IllegalTransactionStateException e) {
			System.err.println("@Transactional(propagation = Propagation.NEVER3) -> OK");
		}

		try {
			service.testReadOnly1();
			System.err.println("@Transactional(readOnly = true)1 -> OK");
		} catch (IllegalTransactionStateException e) {
			System.err.println("@Transactional(readOnly = true)1 -> KO");
		}

		// Questa solleva una JpaSystemException
		try {
			service.testReadOnly2(new Salume("Mortazza"));
			System.err.println("@Transactional(readOnly = true)2 -> KO");
		} catch (JpaSystemException e) {
			System.err.println("@Transactional(readOnly = true)2 -> OK");
		}

		service.resetStatus();
		try {
			Salume salume = new Salume("inserito1");
			service.testRollbackFor1(salume);
			System.err.println("@Transactional(rollbackFor = ExceptionClass.class)1 -> KO");
		} catch (CustomCheckedException1 e) {
			Salume salumeInserito = repository.findByName("inserito1");
			if (salumeInserito != null && salumeInserito.getSalumeId() != null) {
				System.err.println("@Transactional(rollbackFor = ExceptionClass.class)1 -> OK");
			} else {
				System.err.println("@Transactional(rollbackFor = ExceptionClass.class)1 -> KO");
			}
		}

		service.resetStatus();
		try {
			Salume salume = new Salume("inserito2");
			service.testRollbackFor2(salume);
			System.err.println("@Transactional(rollbackFor = ExceptionClass.class)2 -> KO");
		} catch (CustomCheckedException1 e) {
			Salume salumeInserito = repository.findByName("inserito2");
			if (salumeInserito != null && salumeInserito.getSalumeId() != null) {
				System.err.println("@Transactional(rollbackFor = ExceptionClass.class)2 -> KO");
			} else {
				System.err.println("@Transactional(rollbackFor = ExceptionClass.class)2 -> OK");
			}
		}

		service.resetStatus();
		try {
			Salume salume = new Salume("inserito3");
			service.testNoRollbackFor1(salume);
			System.err.println("@Transactional(noRollbackFor = ExceptionClass.class)1 -> KO");
		} catch (CustomUncheckedException1 e) {
			Salume salumeInserito = repository.findByName("inserito3");
			if (salumeInserito != null && salumeInserito.getSalumeId() != null) {
				System.err.println("@Transactional(noRollbackFor = ExceptionClass.class)1 -> OK");
			} else {
				System.err.println("@Transactional(noRollbackFor = ExceptionClass.class)1 -> KO");
			}
		}

		service.resetStatus();
		try {
			Salume salume = new Salume("inserito4");
			service.testNoRollbackFor2(salume);
			System.err.println("@Transactional(noRollbackFor = ExceptionClass.class)2 -> KO");
		} catch (CustomUncheckedException2 e) {
			Salume salumeInserito = repository.findByName("inserito4");
			if (salumeInserito != null && salumeInserito.getSalumeId() != null) {
				System.err.println("@Transactional(noRollbackFor = ExceptionClass.class)2 -> KO");
			} else {
				System.err.println("@Transactional(noRollbackFor = ExceptionClass.class)2 -> OK");
			}
		}

	}

}
