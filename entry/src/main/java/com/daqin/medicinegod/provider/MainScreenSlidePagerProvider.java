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

import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.DependentLayout;
import ohos.agp.components.PageSliderProvider;
import ohos.app.Context;

import java.util.List;

/**
 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in sequence.
 *
 * @since 2021-04-09
 */
public class MainScreenSlidePagerProvider extends PageSliderProvider {
    private final Context context;
    private final List<DependentLayout> fragmentList;

    public MainScreenSlidePagerProvider(Context context, List<DependentLayout> fragmentList) {
        this.context = context;
        this.fragmentList = fragmentList;
    }

    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

    @Override
    public Object createPageInContainer(ComponentContainer componentContainer, int position) {
        DependentLayout fraction = fragmentList.get(position);
        componentContainer.addComponent(fraction);
        return fraction;
    }

    @Override
    public void destroyPageFromContainer(ComponentContainer componentContainer, int position, Object object) {
        Component contentView = (Component) object;
        componentContainer.removeComponent(contentView);
    }

    @Override
    public boolean isPageMatchToObject(Component component, Object o) {
        return component == o;
    }
}