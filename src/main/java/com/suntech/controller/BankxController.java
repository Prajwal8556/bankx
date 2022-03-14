
package com.suntech.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.suntech.domain.Account;
import com.suntech.domain.AccountType;
import com.suntech.domain.Bank;
import com.suntech.domain.Branches;
import com.suntech.domain.Card;
import com.suntech.domain.Customer;
import com.suntech.domain.CustomerQuery;
import com.suntech.domain.Employee;
import com.suntech.domain.Insurance;
import com.suntech.domain.Loans;
import com.suntech.model.AccountOpeningModel;
import com.suntech.service.AccountTypeService;
import com.suntech.service.BankService;
import com.suntech.service.BranchService;
import com.suntech.service.CardService;
import com.suntech.service.CustomerService;
import com.suntech.service.CustomerqueryService;
import com.suntech.service.EmployeeService;
import com.suntech.service.InsuranceService;
import com.suntech.service.LoanService;
import com.suntech.utils.AccountUtils;
import com.suntech.utils.MailServiceUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author PRAJWAL.H R
 *
 */
@RestController
@Component
public class BankxController {

	private static final String SUCCESS_MESSAGE = "Account has been successfully opened.";
	private static final String FAILURE_MESSAGE = "Failed to open account.";

	@Autowired
	private MailServiceUtils mailServiceUtils;

	@Autowired
	private BankService bankService;

	@Autowired
	private AccountUtils accountUtils;

	@Autowired
	private BranchService branchService;

	@Autowired
	private AccountTypeService accountTypeService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomerqueryService customerqueryService;

	@Autowired
	private CardService cardService;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private InsuranceService insuranceService;

	@Autowired
	private LoanService loanService;

	@JmsListener(destination = "${springjms.accountQueue}")
	public void receiveFromAccountQueue(String message) {
		String messageContent;
		String receiverEmail = "";
		try {
			Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
			AccountOpeningModel accountOpeningModel = gson.fromJson(message, AccountOpeningModel.class);

			Customer customer = accountOpeningModel.getCustomer();
			receiverEmail = customer.getEmail();
			customerService.createAndSave(customer);
			Account account = accountOpeningModel.getAccount();
			Long accountNumber = accountUtils.generateAccountNumber();
			account.setAccountNo(accountNumber);
			AccountType accountType = accountOpeningModel.getAccountType();

			messageContent = "Congratulation's you have successfully created your bank account."
					+ " and your accountNumber is " + accountNumber;
			accountType.setAccount(account);
			account.setCustomer(customer);
			account.setAccountType(accountType);
			accountTypeService.createAndSave(accountType);
			mailServiceUtils.sendmail(receiverEmail, messageContent, Boolean.TRUE, SUCCESS_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
			messageContent = "Sorry ,unable to create bank account ;(";
			if (StringUtils.hasText(receiverEmail)) {
				try {
					mailServiceUtils.sendmail(receiverEmail, messageContent, Boolean.FALSE, FAILURE_MESSAGE);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	@JmsListener(destination = "${springjms.customerQueue}")
	public void receiveFromCustomerQueue(String message) {
		Gson gson = new Gson();
		CustomerQuery customerQuery = gson.fromJson(message, CustomerQuery.class);
		customerqueryService.createAndSaveCustomerquery(customerQuery);
	}

	@JmsListener(destination = "${springjms.loanQueue}")
	public void receiveFromLoanQueue(String message) {
		System.out.println("Message==>" + message);
		Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
		Loans loans = gson.fromJson(message, Loans.class);
		loanService.createAndSaveLoans(loans);

	}

	@JmsListener(destination = "${springjms.cardQueue}")
	public void receiveFromcardQueue(String message) {
		Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();

		Card card = gson.fromJson(message, Card.class);
		cardService.addCard(card);

	}

	@ApiOperation(value = "Add a bank", response = Bank.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@PostMapping("/bank")
	public Bank insertBank(@RequestBody() Bank bank) {
		bankService.createAndSaveBank(bank);
		return bank;
	}

	@ApiOperation(value = "Add a Branch", response = Branches.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@PostMapping("/branch")
	public Branches insertBranches(@RequestBody() Branches branches) {
		branchService.createAndSaveBranch(branches);
		return branches;
	}

	@ApiOperation(value = "Add a card", response = Card.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })

	@PostMapping("/card")
	public Card addCard(@RequestBody() Card card) {
		return cardService.addCard(card);
	}

	@ApiOperation(value = "Add a Employee", response = Employee.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@PostMapping("/employee")
	public Employee insertEmployee(@RequestBody() Employee employee) {
		employeeService.createandSave(employee);
		return employee;
	}

	@ApiOperation(value = "Add a Insurance", response = Insurance.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@PostMapping("/insurance")
	public Insurance insertInsurance(@RequestBody() Insurance insurance) {
		insuranceService.createAndSaveInsurance(insurance);
		return insurance;
	}

	@ApiOperation(value = "List of Insurance", response = Insurance.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@GetMapping("/insurance")
	public List<Insurance> getInsurance() {
		return insuranceService.findAll();

	}

	@GetMapping("/insurance/{id}")
	public Insurance getInsurances(@PathVariable() Integer id) {
		return insuranceService.find(id);
	}

}