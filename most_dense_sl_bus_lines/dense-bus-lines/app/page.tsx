"use client";

import React, {useEffect, useState} from "react";
import styles from './page.module.css'
import {BusLineDto} from "@/app/types";
import BusLineCard from "@/app/BusLineCard";

function Home() {
    const [denseBusLinesState, setDenseBusLinesState] = useState<BusLineDto[]>([]);

    async function denseBusLines() {
        const denseBusLise = await fetch(
            'http://localhost:8080/v1/bus/lines/most-dense/top10',
            {
                method: 'GET',
                headers: {
                    'Content-type': 'application/json',
                },
            },
        ).then(response => response.json());
        setDenseBusLinesState(denseBusLise);
    }

    useEffect(() => {
        if (denseBusLinesState.length == 0) {
            denseBusLines();
        }
    });

    if (denseBusLinesState.length == 0) {
        return (
            <main className={styles.main}>
                <div className={styles.vercelLogo}> Loading</div>
            </main>)
    }
    return (
        <main className={styles.main}>
            {
                denseBusLinesState.map(line =>
                    <div key={line.number} className={styles.grid}>
                        <BusLineCard line={line}/>
                    </div>
                )
            }
        </main>
    )
}

export default Home;