import { TestBed } from '@angular/core/testing';
import { CompanyService } from './company.service';
import { Company } from './company';
import { HttpClient } from "@angular/common/http";
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { environment } from 'src/environments/environment';

  describe('Company Service Test', () => {
    let httpTestingController: HttpTestingController;
    let companyService: CompanyService;

    beforeEach(async () => {
      TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    })
    httpTestingController = TestBed.inject(HttpTestingController);
    companyService = TestBed.inject(CompanyService)
  });

  it('calling getCompanies() should invoke GET companies', () => {
    companyService.getCompanies().subscribe(companies => expect(companies).toEqual(expectedCompanies))
    const request = httpTestingController.expectOne(`${environment.apiUrl}/companies/get/all`);
    expect(request.request.method).toBe("GET");
    request.flush(expectedCompanies);
    httpTestingController.verify();
  });

  it('calling addCompany() should invoke POST', () => {
    const company = expectedCompanies[0];
    const expectedId = "1";

    companyService.addCompanies(company).subscribe(id => expect(id).toEqual(expectedId))

    const request = httpTestingController.expectOne(`${environment.apiUrl}/companies/add/`);
    expect(request.request.method).toBe("POST");
    expect(request.request.body).toEqual(
     {
      taxIdentification: '111-111-11-11',
      address: 'Street 1',
      name: 'Company First',
      pensionInsurance: 111.11,
      healthInsurance: 444.44
      }
    );
    request.flush(expectedId);
    httpTestingController.verify();
  });

  const expectedCompanies: Company[] = [
    new Company(
      1,
      '111-111-11-11',
      'Street 1',
      'Company First',
      111.11,
      444.44
    ),
    new Company(
      2,
      '222-222-22-22',
      'Street 2',
      'Company Second',
      222.22,
      555.55
    ),
    new Company(
      3,
      '333-333-33-33',
      'Street 3',
      'Company Third',
      333.33,
      666.66
    )
  ];

});
