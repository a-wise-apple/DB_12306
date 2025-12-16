import axios from 'axios';

const API_URL = 'http://localhost:8080/api/trading';

export interface TicketListing {
    listingId: number;
    price: number;
    sellerName: string;
    trainNumber: string;
    departureStation: string;
    arrivalStation: string;
    departureTime: string;
    arrivalTime: string;
    seatInfo: string;
    status: string;
}

export const getListings = async () => {
    const response = await axios.get<TicketListing[]>(API_URL);
    return response.data;
};

export const createListing = async (userId: number, orderId: number, price: number) => {
    await axios.post(`${API_URL}/list`, { orderId, price }, { params: { userId } });
};

export const buyListing = async (listingId: number, buyerId: number) => {
    await axios.post(`${API_URL}/${listingId}/buy`, {}, { params: { buyerId } });
};

export const cancelListing = async (listingId: number, userId: number) => {
    await axios.post(`${API_URL}/${listingId}/cancel`, {}, { params: { userId } });
};
