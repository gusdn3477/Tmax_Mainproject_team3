import React, {useEffect, useState} from 'react';
import Footer from '../../layout/Footer';
import { Link } from 'react-router-dom';
import Header from '../../layout/Header';
import Bread from '../../elements/ui/Bread';

export default function Home(){

    return(
        <div id="wrap">
            <Header/>
            <Bread name="관리자 모드"/>
                <div className="Container">
                    <div className="login-form">
                        <div className="mb-3">
                        <h3>상품 등록</h3>
                        <div>상품을 등록할 수 있습니다.</div>
                    </div>
                    <Link to="/productmake">
                            <button type="button" className="btn btn-color-orange admin-submit">상품 등록</button>
                    </Link>
                </div>

        <div className="login-form">
            <div className="mb-3">
            <h3>상품 수정 및 삭제</h3>
            <div>상품을 수정 및 삭제할 수 있습니다.</div>
        </div>
        <Link to="/productmodify">
                <button type="button" className="btn btn-color-orange admin-submit">상품 수정/삭제</button>
        </Link>
        </div>
        
</div>
        <Footer/>
      </div>

    );
}