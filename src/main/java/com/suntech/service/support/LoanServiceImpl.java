package com.suntech.service.support;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suntech.dao.LoanDao;
import com.suntech.domain.Loans;
import com.suntech.service.LoanService;

@Service
public class LoanServiceImpl implements LoanService{

	
	@Autowired
	private LoanDao loanDao ;
	
	@Override
	@Transactional
	public Loans createAndSaveLoans(Loans loans) {
		 loanDao.save(loans);
	      return loans;
	}

}
