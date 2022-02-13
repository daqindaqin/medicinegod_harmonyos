/*
 * Copyright (C) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.daqin.medicinegod.provider;

import ohos.agp.components.*;
import ohos.app.Context;

import java.util.List;


public class RgLgScreenSlidePagerProvider extends PageSliderProvider {
    private List<Component> mList;

    public RgLgScreenSlidePagerProvider(List<Component> list) {
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object createPageInContainer(ComponentContainer componentContainer, int i) {
        componentContainer.addComponent(mList.get(i));
        return mList.get(i);
    }

    @Override
    public void destroyPageFromContainer(ComponentContainer componentContainer, int i, Object o) {
        componentContainer.removeComponent(mList.get(i));
    }

    @Override
    public boolean isPageMatchToObject(Component component, Object o) {
        return component == o;
    }

}