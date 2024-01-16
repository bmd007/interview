export type BusLineDto = {
  number: string;
  stops: StopDto[];
  numberOfStops: number;
};

export type StopDto = {
  number: string;
  name: string;
};

