export class Entry {
  private readonly id: string;
  private readonly parameters: object;

  constructor(data) {
    this.id = data.id;
    this.parameters = data.parameters;
  }

  public getId() : string {
    return this.id;
  }

  public getParameter(name : string) : string {
    return this.parameters[name];
  }

  public getParameters() : object {
    return this.copy(this.parameters, {});
  }

  private copy(from, to) : object{
    for(let x in from){
      to[x] = from[x];
    }
    return to;
  }
}
