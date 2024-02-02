package com.service;

import com.entity.Salume;
import com.exception.CustomCheckedException1;

public interface SalumeService {

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

	void testThrowCheckedException() throws Exception;

	void testThrowUncheckedException();

	Boolean testPropagationRequiredNew1();

	Boolean testPropagationRequiredNew2();

	void testPropagationMandatory1();

	void testPropagationMandatory2();

	void testPropagationMandatory3();

	void testPropagationNever1();

	void testPropagationNever2();

	void testPropagationNever3();

	void testReadOnly1();

	void testReadOnly2(Salume salume);

	void testRollbackFor1(Salume salume) throws CustomCheckedException1;

	void testRollbackFor2(Salume salume) throws CustomCheckedException1;

	void testNoRollbackFor1(Salume salume);

	void testNoRollbackFor2(Salume salume);

	void resetStatus();

}
