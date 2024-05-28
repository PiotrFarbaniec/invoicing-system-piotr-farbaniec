
export class Company {
  public editMode: boolean = false;
  public editedCompany?: Company | undefined | null = null;

  constructor(
    public id?: number,
    public taxIdentification?: string,
    public address?: string,
    public name?: string,
    public pensionInsurance?: number,
    public healthInsurance?: number
  ) { }
}
