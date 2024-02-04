  <h1>UniTech App</h1>
    <h2>Description</h2>
    <p>
      Unitech app is a banking app that is the MVP product of banking services
      and back-end services. Also, the correction of calculation, converting, and transferring process was handled carefully with BigDecimal data type.
    </p>
    <h2>Installing</h2>
    <p>Firstly you have to clone this GitHub repo to your local machine. Ensure that <b>Docker Desktop</b>, <b>Maven</b> and <b>JDK</b> have been installed in your PC.</p>
    <h2>Usage</h2>
    <p>
        <ul>
            <li>
                <b>Step 1:</b> <br>
                Initially open Docker Desktop app and a local terminal, change the directory to the exact github repository and firstly run <b>mvn clean install</b> command. Next, the root component you have to run <b>docker-compose up -d</b>  command. This command will fetch images and create a Docker container inside of the Docker Desktop application.
            </li>
            <li> <b>Step 2:</b> <br>            After creating the docker container you have to change direction to(there are 2 modules unitech and currencyapi) unitech folder inside of this style Rectory you have to run <b>mvn spring-boot:run</b>  command and this command will compile our main unitech module.
            </li>
            <li>
                <b>Step 3:</b> <br>
                As a next step, you have to open a new terminal and change the directory to the main folder that you cloned from Git Hub. For this time change the directory to currencyapi folder and run <b> mvn spring-boot:run </b>command. This will compile the third-party Currency API mock service.
            </li>
           <li><b>Final Step:</b> <br>
                You have already compiled both modules and right now you can test them by sending requests to specific paths. I added below all paths which can be tested by anyone.
                Moreover, when the main module is compiled the application inserts DUMMY_DATA to the database. I provided login information below, then you can use it for your testing purposes.
                <br>ADMIN -> <br>
                {<br>
                    "pin": "admin",<br>
                    "password": "password" <br>
                } <br>
                USER -> <br>
                {
                    <br>
                    "pin": "user",<br>
                    "password": "Mahammad" <br>
                }
            </li>
        </ul>
    </p>
      <h2>Roles</h2>
    <p>There are 2 roles(Admin & User) inside of the Unitech app. Each role has specific features.</p>
    <h2>Main Functionalities</h2>
       <p>
        <ul>
            <li>Authentication (Register&Login) powered by JWT token: <br> User can register and log in with  PIN and password which user chooses the Chosen password will be encrypted when it is stored in the database. Also after the registration and login process user gets a JWT token for authorization purposes. This token has an expiration date and after 48 hours JWT token expires automatically.</li>
            <li>Bank Accounts: <br> Users can get all active accounts if they have one. Only the admin can create an account and also change the status of the account.</li>
            <li>Make transfer: <br>User can send money among own accounts and others' accounts too. Each account has accountCurrency and if the sender and receiver account currencies are the same, the service doesn't call any third-party API for converting currency. However, if the currencies are different then the service will send the request to the third-party API(currencyAPI) and get the exchange rate (i.e. - USD/AZN = 1.7) for converting money.
            After the converting process, the money will be transferred to the receiver's account.
            </li>
          <li>
            Currency API: <br> This is a third-party mock service where the user can get random information(because of testing purposes.Period was handled in another service) with given inputs(i.e. from:USD, to:AZN) about the current exchange rate and convert money while transferring money. Let's assume that each request to this API has cost and the bank should decrease the request count. Another service handles this problem.
          </li>
          <li>CurrencyCacheService: <br> 
              For increasing efficiency and reducing cost Spring Cache system was used here. The cache is internal data storage where the service can store some data and call them. 
            In our app, CurrencyCacheService has its own functionalities for reducing cost and increasing efficiency. <br>
            Let's explain with a real-world example:
            When the user wants to send money from USD account to AZN account, he sends a request and our service fetches data for USD/AZN and stores it in the cache. When it fetches USD/AZN, the service automatically converts USD/AZN to AZN/USD and stores it in the cache too. 
            In this way, we reduce the request count 2 times. Moreover, next 1-minute period both values are stored in the cache, and during this time frame if other requests also come service does not send new requests and gets data from the cache.
            After 1 minute cache clears itself automatically and it helps us display up-to-date exchange rates to users. Finally, if the user does not send a new request API will not be called by service and we reduce cost and increase efficiency for our application.
          </li>
        </ul>
    </p>
    <h2>Testing</h2>
    <p>
      The reliability of the Unitech app is provided with unit and integration tests. The minimum coverage for our app is approx 70%. There are 24 tests and if you want to run these tests you have to change the directory to the main folder(Unitech). After that you have to run <b>mvn test</b> command, so this command will build all test cases. 
    </p>
    <h2>Path</h2>
    <p>As I mentioned before, I added below all possible testing paths. Moreover, I mentioned the database username and password so that you can check the database too.</p> <br>
    <p> Ports:</p>
    <ul>
      <li>unitech: 8081</li>
      <li>currencyapi: 8082</li>
    </ul>
      <p> Database:</p>
    <ul>
      <li>username: unitech</li>
      <li>password: password</li>
    </ul>
    <ul>
      <li>-ADMIN Only- GET all registered users -> localhost:8081/api/v1/auth/users</li>
      <li>Registration POST request. You have to send pin and password for registering -> localhost:8081/api/v1/auth/register</li>
      <li>Login POST request. Send registered pin and password in body -> localhost:8081/api/v1/auth/login</li>
      <li>Get all active account of user -> localhost:8081/api/v1/account/myaccounts</li>
      <li>-ADMIN Only- Get all accounts in the system -> localhost:8081/api/v1/account/allaccounts</li>
      <li>-ADMIN Only- Change account status-> localhost:8081/api/v1/account/changeaccountstatus</li>
      <li>-ADMIN Only- Create a new account. You have to add account userPin (for which user), balance, and currency. -> localhost:8081/api/v1/account/createaccount</li>
      <li>-ADMIN Only- Get all transactions -> localhost:8081/api/v1/transaction/transactions</li>
      <li>Get current exchange rate for selected currencies -> localhost:8081/api/v1/currency/rate?from=EUR&to=AZN</li>
      <li>Make a transfer to another account. You must add senderAccount, receiverAccount and transactionBalance. -> localhost:8081/api/v1/transaction/send</li>
      <li>If you want to check currencyAPI itself -> localhost:8082/tpapi/v1/rate?fromCurrency=USD&toCurrency=AZN</li>
    </ul>
