package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.entity.Salume;
import com.repository.SalumeRepository;

@Service
public class SalumeServiceImpl2 implements SalumeService2 {

	@Autowired
	private SalumeRepository repo;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Integer testPropagationRequiredNew() {
		/*
		 * Propagation.REQUIRES_NEW significa che viene SEMPRE creata una nuova
		 * transazione. Questo significa che se gia' esiste una transazione (ovvero se
		 * questo metodo e' stato invocato da un altro metodo che ha gia' creato una
		 * transazione ad esempio), la transazione esistente viene sospesa e ne viene
		 * creata una nuova all'interno della quale vengono eseguite le operazioni sul
		 * DB di questo metodo.
		 * 
		 * Questo significa che se il metodo invocante crea una propria transazione, fa
		 * delle insert e invoca un metodo, il metodo invocato (nell'esempio, questo)
		 * non vede i record inseriti dal primo in quanto ancora non e' stato fatto il
		 * commit della transazione (creata dal metodo invocante).
		 * 
		 * Questo significa altresi' che se il metodo invocante fa delle insert ma non
		 * all'interno di una transazione (quindi non c'e' una transazione esistente),
		 * il metodo invocato (nell'esempio, questo) e' in grado di vedere i record
		 * inseriti dal metodo invocante in quanto gia' committati.
		 */
		repo.save(new Salume("salume1"));
		repo.save(new Salume("salume2"));
		repo.save(new Salume("salume3"));
		return repo.findAll().size();
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void testPropagationMandatory() {
	}

	@Override
	@Transactional(propagation = Propagation.NEVER)
	public void testPropagationNever() {
	}

}
