import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

export interface Station {
    id: number;
    name: string;
}

export interface Train {
    id: number;
    trainNo: string;
    trainType: string;
}

export const getStations = async () => {
    const response = await axios.get<Station[]>(`${API_URL}/stations`);
    return response.data;
};

export const getTrains = async () => {
    const response = await axios.get<Train[]>(`${API_URL}/trains`);
    return response.data;
};
