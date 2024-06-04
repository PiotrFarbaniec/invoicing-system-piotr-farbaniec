import { TestBed, ComponentFixture } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { Company } from './company';
import { CompanyService } from './company.service';
import { FormsModule } from '@angular/forms';
import { of } from 'rxjs';


describe('AppComponent', () => {
  let fixture: ComponentFixture<AppComponent>;
  let component: AppComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [{ provide: CompanyService, useClass: MockCompanyService }],
      declarations: [AppComponent],
      imports: [FormsModule]
    }).compileComponents();

      fixture = TestBed.createComponent(AppComponent);
      component = fixture.componentInstance;
      component.ngOnInit();
      fixture.detectChanges();
  });


  it('should display a list of all Companies', () => {
    const expectedCompanies = MockCompanyService.companies;

    // Assert the headers
    expect(fixture.nativeElement.innerText).toContain('Tax identification');
    expect(fixture.nativeElement.innerText).toContain('Address');
    expect(fixture.nativeElement.innerText).toContain('Name');
    expect(fixture.nativeElement.innerText).toContain('Pension insurance');
    expect(fixture.nativeElement.innerText).toContain('Health insurance');

    // Assert each company's details
    for (const company of expectedCompanies) {
        expect(fixture.nativeElement.innerText).toContain(company.taxIdentification);
        expect(fixture.nativeElement.innerText).toContain(company.address);
        expect(fixture.nativeElement.innerText).toContain(company.name);
        expect(fixture.nativeElement.innerText).toContain(company.pensionInsurance);
        expect(fixture.nativeElement.innerText).toContain(company.healthInsurance);
    }

    // Assert the component's companies property
    expect(component.companies).toEqual(expectedCompanies);
  });


  it('newly added company is added to the list', () => {
    const taxIdentificationInput: HTMLInputElement = fixture.nativeElement.querySelector('input[name=taxIdentification]');
    taxIdentificationInput.value = '444-444-44-44';
    taxIdentificationInput.dispatchEvent(new Event('input'));

    const addressInput: HTMLInputElement = fixture.nativeElement.querySelector('input[name=address]');
    addressInput.value = 'Street 4';
    addressInput.dispatchEvent(new Event('input'));

    const nameInput: HTMLInputElement = fixture.nativeElement.querySelector('input[name=name]');
    nameInput.value = 'Company Fourth';
    nameInput.dispatchEvent(new Event('input'));


    const pensionInsuranceInput: HTMLInputElement = fixture.nativeElement.querySelector('input[name=pensionInsurance]');
    pensionInsuranceInput.value = '888.88';
    pensionInsuranceInput.dispatchEvent(new Event('input'));

    const healthInsuranceInput: HTMLInputElement = fixture.nativeElement.querySelector('input[name=healthInsurance]');
    healthInsuranceInput.value = '999.99';
    healthInsuranceInput.dispatchEvent(new Event('input'));


    const addInvoiceBtn: HTMLElement = fixture.nativeElement.querySelector('#addCompanyBtn');
    addInvoiceBtn.click();

    fixture.detectChanges();
    expect(fixture.nativeElement.innerText).toContain(
    '444-444-44-44  Street 4  Company Fourth  888.88  999.99'
    );
    console.log(fixture.nativeElement.innerText)
  });


class MockCompanyService {
  static companies: Company[] = [
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

    getCompanies() {
    return of(MockCompanyService.companies)
    }

    addCompany(company: Company){
      MockCompanyService.companies.push(company);
    return of();
    }
  }

});
