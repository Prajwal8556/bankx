
package com.suntech.controller;

import java.util.List;

import org.dom4j.Branch;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
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
import com.suntech.service.AccountService;
import com.suntech.service.AccountTypeService;
import com.suntech.service.BankService;
import com.suntech.service.BranchService;
import com.suntech.service.CardService;
import com.suntech.service.CustomerService;
import com.suntech.service.CustomerqueryService;
import com.suntech.service.EmployeeService;
import com.suntech.service.InsuranceService;
import com.suntech.service.LoanService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Component
public class BankxController {

	@Autowired
	private BankService bankService;

	@Autowired
	private BranchService branchService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountTypeService accountTypeService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomerqueryService customerqueryService;

	@Autowired
	private CardService cardService;
	private EmployeeService employeeService;
	@Autowired
	private InsuranceService insuranceService;

	@Autowired
	private LoanService loanService;

	@Value("${springjms.accountQueue}")
	private String queue;

	@JmsListener(destination = "${springjms.accountQueue}")
	public void receiveFromAccountQueue(String message)

	{
		System.out.println("Message Received===>" + message);

		Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
		AccountOpeningModel accountOpeningModel = gson.fromJson(message, AccountOpeningModel.class);

		Customer customer = accountOpeningModel.getCustomer();
		customerService.createAndSave(customer);

		AccountType accountType = accountOpeningModel.getAccountType();
		Account account = accountOpeningModel.getAccount();
		accountType.setAccount(account);
		account.setAccountType(accountType);
		accountTypeService.createAndSave(accountType);

		Account account2 = accountService.createAndSave(account);

	}

	@JmsListener(destination = "${springjms.customerQueue}")
	public void receiveFromCustomerQueue(String message) {
		System.out.println("Message==>" + message);

		JSONObject jsonObj = new JSONObject(message);
		Gson gson = new Gson();
		CustomerQuery customerQuery = gson.fromJson(message, CustomerQuery.class);
		customerqueryService.createAndSaveCustomerquery(customerQuery);

		System.out.println(jsonObj);
	}

	@JmsListener(destination = "${springjms.loanQueue}")
	public void receiveFromLoanQueue(String message) {
		System.out.println("Message==>" + message);

		JSONObject jsonObj = new JSONObject(message);
		Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();

		Loans loans = gson.fromJson(message, Loans.class);
		System.out.println(loans.toString());
		loanService.createAndSaveLoans(loans);

	}

	@JmsListener(destination = "${springjms.cardQueue}")
	public void receiveFromcardQueue(String message)

	{
		System.out.println("Message Received===>" + message);

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

//	Employee API
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
	@ApiOperation(value = "get a Insurance", response = Insurance.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@GetMapping("/insurance/{id}")
	public Insurance getInsurances(@PathVariable() Integer id) {
		return insuranceService.find(id);
	}

}