  export class Group {
      readonly name: string;
      readonly type: string;
      readonly parameters: string[];
      readonly subGroups: object[];

    constructor(data) {
      this.name = data.name;
      this.type = data.type;
      this.parameters = data.parameters;
      this.subGroups = data.subGroups;
    }

    public canEditEntries(): boolean {
      return this.type != 'none';
    }

    public canAddEntries(): boolean {
      return this.type != 'none' && this.type != 'basic.simple';
    }

  }
