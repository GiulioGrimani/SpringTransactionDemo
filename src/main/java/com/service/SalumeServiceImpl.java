package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.entity.Salume;
import com.exception.CustomCheckedException1;
import com.exception.CustomCheckedException2;
import com.exception.CustomUncheckedException1;
import com.exception.CustomUncheckedException2;
import com.repository.SalumeRepository;

import jakarta.persistence.EntityManager;

@Service
public class SalumeServiceImpl implements SalumeService {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private SalumeRepository repo;

	@Autowired
	private SalumeService2 service2;

	/*
	 * La prima cosa che vogliamo verificare e' il fatto che il verificarsi di una
	 * checked exception ROMPE la transazione: NON viene effettuato il rollback.
	 * 
	 * La seconda cosa che verifico e' che se si verifica una unchecked exception,
	 * questa NON ROMPE la transazione e pertanto viene effettuato il rollback.
	 */

	@Override
	@Transactional
	public void testThrowCheckedException() throws Exception {
		repo.save(new Salume("Salume1 inserito per colpa di una checked"));
		repo.save(new Salume("Salume2 inserito per colpa di una checked"));
		repo.save(new Salume("Salume3 inserito per colpa di una checked"));
		throw new Exception();
	}

	@Override
	@Transactional
	public void testThrowUncheckedException() {
		repo.save(new Salume("Salume1 non inserito per via di una runtime"));
		repo.save(new Salume("Salume1 non inserito per via di una runtime"));
		repo.save(new Salume("Salume1 non inserito per via di una runtime"));
		throw new RuntimeException();
	}

	/*
	 * Vogliamo testare i seguenti attributi sul @Transactional:
	 * 
	 * - @Transactional(propagation = Propagation.REQUIRES_NEW)
	 * 
	 * - @Transactional(propagation = Propagation.MANDATORY)
	 * 
	 * - @Transactional(propagation = Propagation.NEVER)
	 * 
	 * - @Transactional(readOnly = true)
	 * 
	 * - @Transactional(rollbackFor = ExceptionClass.class)
	 * 
	 * - @Transactional(noRollbackFor = ExceptionClass.class)
	 */

	/*
	 * Test di:
	 * 
	 * @Transactional(propagation = Propagation.REQUIRES_NEW)
	 * 
	 * Questo metodo apre una transazione e invoca un altro metodo di un altro
	 * service annotato con REQUIRES_NEW, che quindi verra' eseguito in una nuova
	 * transazione e quindi non vedra' le modifiche della prima. Per questo, la
	 * transazione "padre" vedra' tutti i salumi inseriti (sia da se stessa che
	 * dalla transazione "figlia") mentre la transazione "figlia" vedra' solo i
	 * salumi inseriti da se stessa
	 */
	@Override
	@Transactional
	public Boolean testPropagationRequiredNew1() {
		repo.save(new Salume("Mortazza"));
		Integer countOnOtherTransaction = service2.testPropagationRequiredNew();
		Integer count = repo.findAll().size();
		return (countOnOtherTransaction == 3 && count == 4);
	}

	/*
	 * Test di:
	 * 
	 * @Transactional(propagation = Propagation.REQUIRES_NEW)
	 * 
	 * Questo metodo NON apre una transazione, fa il save di un salume e quindi ne
	 * fa anche il commit, inserendolo a tutti gli effetti nel DB. Il metodo
	 * successivamente invoca un altro metodo di un altro service annotato con
	 * REQUIRES_NEW, che quindi crea una transazione se non esiste, fa il save di 3
	 * salumi e restituisce il count di tutti i salumi che vede sul DB, che
	 * ammontano a 4 in quanto il salume inserito dal metodo di questa classe non
	 * sta dentro una transazione (commit gia' fatto)
	 */
	@Override
	public Boolean testPropagationRequiredNew2() {
		repo.save(new Salume("Mortazza"));
		Integer countOnOtherTransaction = service2.testPropagationRequiredNew();
		Integer count = repo.findAll().size();
		return (countOnOtherTransaction == count);
	}

	/*
	 * Test 1 di:
	 * 
	 * @Transactional(propagation = Propagation.MANDATORY)
	 * 
	 * Mi aspetto che questo metodo sollevi un'eccezione in quanto deve essere
	 * eseguito all'interno di una transazione gia' esistente, ma questa non esiste
	 * all'interno dello stesso proxy
	 */
	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void testPropagationMandatory1() {
	}

	/*
	 * Test 2 di:
	 * 
	 * @Transactional(propagation = Propagation.MANDATORY)
	 * 
	 * Mi aspetto che questo metodo NON sollevi un'eccezione in quanto crea una
	 * transazione e invoca un altro metodo di un altro service che richiede
	 * obbligatoriamente di trovarsi in una transazione
	 */
	@Override
	@Transactional
	public void testPropagationMandatory2() {
		service2.testPropagationMandatory();
	}

	/*
	 * Test 3 di:
	 * 
	 * @Transactional(propagation = Propagation.MANDATORY)
	 * 
	 * Mi aspetto che questo metodo sollevi un'eccezione in quanto non crea una
	 * transazione, ma invoca un metodo di un altro service che richiede di essere
	 * inserito all'interno di una transazione gia' esistente
	 */
	@Override
	public void testPropagationMandatory3() {
		service2.testPropagationMandatory();
	}

	/*
	 * Test 1 di:
	 * 
	 * @Transactional(propagation = Propagation.NEVER)
	 * 
	 * Mi aspetto che questo metodo NON sollevi un'eccezione in quanto viene
	 * eseguito fuori da un contesto transazione, cosi' come richiede
	 */
	@Override
	@Transactional(propagation = Propagation.NEVER)
	public void testPropagationNever1() {
	}

	/*
	 * Test 2 di:
	 * 
	 * @Transactional(propagation = Propagation.NEVER)
	 * 
	 * Mi aspetto che questo metodo NON sollevi un'eccezione in quanto non crea una
	 * transazione e invoca un metodo di un altro service che richiede di non essere
	 * eseguito all'interno di una transazione
	 */
	@Override
	public void testPropagationNever2() {
		service2.testPropagationNever();
	}

	/*
	 * Test 3 di:
	 * 
	 * @Transactional(propagation = Propagation.NEVER)
	 * 
	 * Mi aspetto che questo metodo sollevi un'eccezione in quanto crea una
	 * transazione e invoca un metodo di un altro service che richiede di non essere
	 * eseguito all'interno di una transazione
	 */
	@Override
	@Transactional
	public void testPropagationNever3() {
		service2.testPropagationNever();
	}

	/*
	 * Test 1 di:
	 * 
	 * @Transactional(readOnly = true)
	 * 
	 * Mi aspetto che questo metodo NON sollevi un'eccezione in quanto non esegue
	 * azioni di scrittura, ma di sola lettura
	 */
	@Override
	@Transactional(readOnly = true)
	public void testReadOnly1() {
		repo.findAll();
	}

	/*
	 * Test 2 di:
	 * 
	 * @Transactional(readOnly = true)
	 * 
	 * Mi aspetto che questo metodo sollevi un'eccezione in quanto esegue azioni di
	 * scrittura
	 */
	@Override
	@Transactional(readOnly = true)
	public void testReadOnly2(Salume salume) {
		repo.save(salume);
	}

	/*
	 * Test 1 di:
	 * 
	 * @Transactional(rollbackFor = CustomCheckedException2.class)
	 * 
	 * Questo metodo fara' il rollback solo se viene lanciata una eccezione
	 * unchecked (RuntimeException) oppure quella indicata nel rollbackFor, che e'
	 * una CustomCheckedException2. Dal momento che viene sollevata
	 * CustomCheckedException1, mi aspetto che il save del salume venga committato
	 */
	@Override
	@Transactional(rollbackFor = CustomCheckedException2.class)
	public void testRollbackFor1(Salume salume) throws CustomCheckedException1 {
		repo.save(salume);
		throw new CustomCheckedException1("Eccezione custom1");
	}

	/*
	 * Test 2 di:
	 * 
	 * @Transactional(rollbackFor = CustomCheckedException1.class)
	 * 
	 * Questo metodo fara' il rollback solo se viene sollevata
	 * CustomCheckedException1 (oppure una RuntimeException, come da default). Dal
	 * momento che viene sollevata CustomCheckedException1, mi aspetto che il save
	 * del salume NON venga committato (quindi che faccia il rollback)
	 */
	@Override
	@Transactional(rollbackFor = CustomCheckedException1.class)
	public void testRollbackFor2(Salume salume) throws CustomCheckedException1 {
		repo.save(salume);
		throw new CustomCheckedException1("Eccezione custom1");
	}

	/*
	 * Test 1 di:
	 * 
	 * @Transactional(noRollbackFor = CustomUncheckedException1.class)
	 * 
	 * Questo metodo NON fara' il rollback se viene sollevata
	 * CustomUncheckedException1. Dal momento che viene sollevata
	 * CustomUncheckedException1, mi aspetto che il save del salume venga committato
	 */
	@Override
	@Transactional(noRollbackFor = CustomUncheckedException1.class)
	public void testNoRollbackFor1(Salume salume) {
		repo.save(salume);
		throw new CustomUncheckedException1("Eccezione custom1");
	}

	/*
	 * Test 2 di:
	 * 
	 * @Transactional(noRollbackFor = CustomCheckedException1.class)
	 * 
	 * Questo metodo NON fara' il rollback se viene sollevata
	 * CustomUncheckedException1. Dal momento che viene sollevata
	 * CustomUncheckedException2, mi aspetto che faccia il rollback, ovvero mi
	 * aspetto che il save del salume NON venga committato
	 */
	@Override
	@Transactional(noRollbackFor = CustomUncheckedException1.class)
	public void testNoRollbackFor2(Salume salume) {
		repo.save(salume);
		throw new CustomUncheckedException2("Eccezione custom2");
	}

	@Override
	@Transactional
	public void resetStatus() {
		repo.deleteAll();
		String resetAutoIncrementSql = "ALTER TABLE salumi.salume AUTO_INCREMENT = 1";
		entityManager.createNativeQuery(resetAutoIncrementSql).executeUpdate();
	}

}
